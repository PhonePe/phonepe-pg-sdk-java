/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.phonepe.sdk.pg.common.events.publisher;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.Constants;
import com.phonepe.sdk.pg.common.events.models.BaseEvent;
import com.phonepe.sdk.pg.common.events.models.BulkEvent;
import com.phonepe.sdk.pg.common.events.queue.EventQueue;
import com.phonepe.sdk.pg.common.http.HttpCommand;
import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import com.phonepe.sdk.pg.common.http.HttpMethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class QueuedEventPublisher implements EventPublisher {

    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final EventQueue eventQueue;
    private final String hostUrl;
    private Supplier<String> authTokenSupplier;
    protected ScheduledExecutorService scheduler;

    @Builder
    public QueuedEventPublisher(
            ObjectMapper objectMapper,
            OkHttpClient okHttpClient,
            EventQueue eventQueue,
            String hostUrl) {
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
        this.eventQueue = eventQueue;
        this.hostUrl = hostUrl;
    }

    @Override
    public void setAuthTokenSupplier(Supplier<String> authTokenSupplier) {
        this.authTokenSupplier = authTokenSupplier;
    }

    @Override
    public void startPublishingEvents(Supplier<String> authTokenSupplier) {
        this.setAuthTokenSupplier(authTokenSupplier);
        if (Objects.isNull(scheduler)) {
            scheduler = Executors.newScheduledThreadPool(1);
            this.scheduler.scheduleWithFixedDelay(
                    this, Constants.INITIAL_DELAY, Constants.DELAY, TimeUnit.SECONDS);
        }
    }

    @Override
    public void send(BaseEvent event) {
        this.eventQueue.add(event);
    }

    private void sendBatchData() {
        try {
            if (this.eventQueue.isEmpty()) {
                return;
            }
            log.info("Queue Size {}", this.eventQueue.size());

            List<List<BaseEvent>> bulkEventBatch = createEventBatches();

            for (List<BaseEvent> sdkEventList : bulkEventBatch) {
                try {
                    sendBatchData(sdkEventList);
                } catch (Exception exception) {
                    log.error("Error occurred sending events batch to backend", exception);
                }
            }
        } catch (Exception exception) {
            log.error("Error occurred sending events batch to backend", exception);
        }
    }

    private List<List<BaseEvent>> createEventBatches() {
        final int CUR_QUEUE_SIZE = this.eventQueue.size();
        List<List<BaseEvent>> bulkEventBatch = new ArrayList<>();
        List<BaseEvent> currentBatch = new ArrayList<>();
        for (int numEventsProcessed = 0;
                numEventsProcessed < CUR_QUEUE_SIZE;
                numEventsProcessed++) {
            BaseEvent event = this.eventQueue.poll();
            if (Objects.isNull(event)) {
                break;
            }
            currentBatch.add(event);
            if (currentBatch.size() == Constants.MAX_EVENTS_IN_BATCH) {
                bulkEventBatch.add(currentBatch);
                currentBatch = new ArrayList<>();
            }
        }
        if (!currentBatch.isEmpty()) {
            bulkEventBatch.add(currentBatch);
        }

        return bulkEventBatch;
    }

    private List<HttpHeaderPair> getHeaders() {
        List<HttpHeaderPair> headers = new ArrayList<>();
        headers.add(HttpHeaderPair.builder().key(Headers.ACCEPT).value(APPLICATION_JSON).build());
        headers.add(
                HttpHeaderPair.builder()
                        .key(Constants.AUTHORIZATION)
                        .value(authTokenSupplier.get())
                        .build());
        headers.add(
                HttpHeaderPair.builder().key(Headers.CONTENT_TYPE).value(APPLICATION_JSON).build());
        return headers;
    }

    @SneakyThrows
    private void sendBatchData(List<BaseEvent> sdkEventList) {
        BulkEvent bulkEvent = BulkEvent.builder().events(sdkEventList).build();
        List<HttpHeaderPair> headers = getHeaders();
        HttpCommand<Object, BulkEvent> httpCommand = buildHttpCommand(headers, bulkEvent);
        httpCommand.execute();
    }

    private HttpCommand<Object, BulkEvent> buildHttpCommand(
            final List<HttpHeaderPair> headers, BulkEvent bulkEvent) {
        return HttpCommand.<Object, BulkEvent>builder()
                .methodName(HttpMethodType.POST)
                .hostURL(this.hostUrl)
                .url(Constants.EVENTS_ENDPOINT)
                .client(this.okHttpClient)
                .objectMapper(this.objectMapper)
                .responseTypeReference(new TypeReference<Object>() {})
                .requestData(bulkEvent)
                .encodingType(APPLICATION_JSON)
                .headers(headers)
                .build();
    }

    @Override
    public void run() {
        sendBatchData();
    }
}
