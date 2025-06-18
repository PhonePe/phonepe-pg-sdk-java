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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.common.events.Constants;
import com.phonepe.sdk.pg.common.events.queue.BoundedConcurrentLinkedQueue;
import java.util.Objects;
import lombok.Builder;
import okhttp3.OkHttpClient;

@Builder
public class EventPublisherFactory {

    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final String hostUrl;
    private static EventPublisher cachedEventPublisher;

    public EventPublisherFactory(
            final ObjectMapper objectMapper,
            final OkHttpClient okHttpClient,
            final String hostUrl) {
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
        this.hostUrl = hostUrl;
    }

    public static void setCachedEventPublisher(EventPublisher eventPublisher) {
        EventPublisherFactory.cachedEventPublisher = eventPublisher;
    }

    public EventPublisher getEventPublisher(boolean shouldPublishEvents) {
        if (shouldPublishEvents) {
            // For different clients, the eventSender should be same
            if (Objects.isNull(cachedEventPublisher)) {
                EventPublisherFactory.setCachedEventPublisher(
                        QueuedEventPublisher.builder()
                                .eventQueue(
                                        new BoundedConcurrentLinkedQueue(Constants.QUEUE_MAX_SIZE))
                                .objectMapper(this.objectMapper)
                                .hostUrl(this.hostUrl)
                                .okHttpClient(this.okHttpClient)
                                .build());
            }
            return EventPublisherFactory.cachedEventPublisher;
        }
        return new EventPublisher() {
            @Override
            public void run() {}
        };
    }
}
