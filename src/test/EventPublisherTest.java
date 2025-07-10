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
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.Constants;
import com.phonepe.sdk.pg.common.events.models.BaseEvent;
import com.phonepe.sdk.pg.common.events.models.BulkEvent;
import com.phonepe.sdk.pg.common.events.models.enums.EventState;
import com.phonepe.sdk.pg.common.events.models.enums.EventType;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisher;
import com.phonepe.sdk.pg.common.events.publisher.QueuedEventPublisher;
import com.phonepe.sdk.pg.common.events.queue.BoundedConcurrentLinkedQueue;
import com.phonepe.sdk.pg.common.events.queue.EventQueue;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.common.tokenhandler.TokenConstants;
import com.phonepe.sdk.pg.common.tokenhandler.TokenService;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class EventPublisherTest extends BaseSetupWithOAuth {

    private EventPublisher eventPublisher;
    private EventQueue eventQueue;

    @BeforeEach
    void setup() {
        this.eventQueue = new BoundedConcurrentLinkedQueue(15);
        this.eventPublisher =
                QueuedEventPublisher.builder()
                        .eventQueue(eventQueue)
                        .objectMapper(objectMapper)
                        .okHttpClient(okHttpClient)
                        .hostUrl("http://localhost:30419")
                        .build();
        eventPublisher.setAuthTokenSupplier(tokenService::getAuthToken);
    }

    @Test
    void testPublishingEvents() {
        wireMockServer.resetRequests();
        String apiPath = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .amount(amount)
                        .merchantOrderId(merchantOrderId)
                        .build();

        BaseEvent event =
                BaseEvent.buildStandardCheckoutPayEvent(
                        EventState.SUCCESS,
                        standardCheckoutPayRequest,
                        apiPath,
                        EventType.PAY_SUCCESS);

        List<BaseEvent> listOfEvents = Arrays.asList(event, event, event);
        for (BaseEvent singleEvent : listOfEvents) {
            this.eventPublisher.send(singleEvent);
        }

        String url = Constants.EVENTS_ENDPOINT;
        PhonePeResponse phonePeResponse =
                PhonePeResponse.builder().message("Success").code("SUCCESS").success(true).build();
        Map<String, String> headers = getEventHeaders();

        BulkEvent bulkEvent =
                BulkEvent.builder()
                        .events(listOfEvents)
                        .clientVersion(Headers.SDK_TYPE + ":" + Headers.SDK_VERSION)
                        .source(Constants.SOURCE)
                        .build();

        Assertions.assertEquals(3, this.eventQueue.size());

        addStubForPostRequest(
                url, headers, bulkEvent, HttpStatus.SC_OK, ImmutableMap.of(), phonePeResponse);

        this.eventPublisher.run();

        wireMockServer.verify(1, postRequestedFor(urlPathMatching(url)));
        Assertions.assertEquals(0, this.eventQueue.size());
    }

    @Test
    void testQueueSizeFullNoMoreEventsAdded() throws IOException {
        String apiPath = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .amount(amount)
                        .merchantOrderId(merchantOrderId)
                        .build();

        BaseEvent event =
                BaseEvent.buildStandardCheckoutPayEvent(
                        EventState.SUCCESS,
                        standardCheckoutPayRequest,
                        apiPath,
                        EventType.PAY_SUCCESS);

        List<BaseEvent> listOfEvents = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            listOfEvents.add(event);
        }
        for (BaseEvent singleEvent : listOfEvents) {
            this.eventQueue.add(singleEvent);
        }
        // Pushed 20 events but actually only MAX_SIZE events were pushed in queue
        Assertions.assertEquals(15, this.eventQueue.size());
    }

    @Test
    void testBatchSizeWhilePushing() throws IOException {
        wireMockServer.resetRequests();
        TokenService.setOAuthResponse(null);
        eventQueue.getQueue().clear();
        String apiPath = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .amount(amount)
                        .merchantOrderId(merchantOrderId)
                        .build();

        BaseEvent event =
                BaseEvent.buildStandardCheckoutPayEvent(
                        EventState.SUCCESS,
                        standardCheckoutPayRequest,
                        apiPath,
                        EventType.PAY_SUCCESS);

        List<BaseEvent> listOfEvents = new ArrayList<>();

        // Pushing events more than BATCH_SIZE ie 10
        for (int i = 0; i < Constants.MAX_EVENTS_IN_BATCH + 2; i++) {
            listOfEvents.add(event);
        }
        for (BaseEvent singleEvent : listOfEvents) {
            this.eventQueue.add(singleEvent);
        }
        String url = Constants.EVENTS_ENDPOINT;
        PhonePeResponse phonePeResponse =
                PhonePeResponse.builder().message("Success").code("SUCCESS").success(true).build();
        Map<String, String> headers = getEventHeaders();

        BulkEvent bulkEventFirstCall =
                BulkEvent.builder()
                        .events(new ArrayList<>())
                        .clientVersion(Constants.CLIENT_VERSION)
                        .source(Constants.SOURCE)
                        .build();
        // First Call will be made with MAX BATCH_SIZE
        for (int i = 0; i < Constants.MAX_EVENTS_IN_BATCH; i++) {
            bulkEventFirstCall.getEvents().add(event);
        }

        // Second Call will be made with remaining events 12 - 10 = 2
        BulkEvent bulkEventSecondCall =
                BulkEvent.builder()
                        .events(Arrays.asList(event, event))
                        .clientVersion(Constants.CLIENT_VERSION)
                        .source(Constants.SOURCE)
                        .build();

        Assertions.assertEquals(12, this.eventQueue.size());

        addStubForPostRequest(
                url,
                headers,
                bulkEventFirstCall,
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                phonePeResponse);

        addStubForPostRequest(
                url,
                headers,
                bulkEventSecondCall,
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                phonePeResponse);
        this.eventPublisher.run();

        // Second Call Made with Remaining Size ie 2 of Bulk Events
        BulkEvent actualSecondCall =
                this.objectMapper.readValue(
                        wireMockServer.getAllServeEvents().get(0).getRequest().getBody(),
                        BulkEvent.class);

        // First Call Made with BATCH_MAX_SIZE ie 10 of Bulk Events
        BulkEvent actualFirstCall =
                this.objectMapper.readValue(
                        wireMockServer.getAllServeEvents().get(1).getRequest().getBody(),
                        BulkEvent.class);

        Assertions.assertEquals(2, actualSecondCall.getEvents().size());
        Assertions.assertEquals(10, actualFirstCall.getEvents().size());
        wireMockServer.verify(2, postRequestedFor(urlPathMatching(url)));
        Assertions.assertEquals(0, this.eventQueue.size());
    }

    @Test
    void testQueueFullBatchSizePush() throws IOException {
        wireMockServer.resetRequests();
        TokenService.setOAuthResponse(null);
        String apiPath = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .amount(amount)
                        .merchantOrderId(merchantOrderId)
                        .build();

        BaseEvent event =
                BaseEvent.buildStandardCheckoutPayEvent(
                        EventState.SUCCESS,
                        standardCheckoutPayRequest,
                        apiPath,
                        EventType.PAY_SUCCESS);

        List<BaseEvent> listOfEvents = new ArrayList<>();

        // Pushing events more than BATCH_SIZE ie 10
        for (int i = 0; i < 20; i++) {
            listOfEvents.add(event);
        }

        // Will push 15 events, as the MAX_QUEUE_SIZE is 15
        for (BaseEvent singleEvent : listOfEvents) {
            this.eventQueue.add(singleEvent);
        }
        String url = Constants.EVENTS_ENDPOINT;
        PhonePeResponse phonePeResponse =
                PhonePeResponse.builder().message("Success").code("SUCCESS").success(true).build();
        Map<String, String> headers = getEventHeaders();

        BulkEvent bulkEventFirstCall =
                BulkEvent.builder()
                        .events(new ArrayList<>())
                        .clientVersion(Headers.SDK_TYPE + ":" + Headers.SDK_VERSION)
                        .source(Constants.SOURCE)
                        .build();

        // First Call will be made with MAX BATCH_SIZE
        for (int i = 0; i < Constants.MAX_EVENTS_IN_BATCH; i++) {
            bulkEventFirstCall.getEvents().add(event);
        }

        // Second Call will be made with remaining events 15 - 10 = 5
        BulkEvent bulkEventSecondCall =
                BulkEvent.builder()
                        .events(Arrays.asList(event, event, event, event, event))
                        .clientVersion(Headers.SDK_TYPE + ":" + Headers.SDK_VERSION)
                        .source(Constants.SOURCE)
                        .build();

        Assertions.assertEquals(15, this.eventQueue.size());

        addStubForPostRequest(
                url,
                headers,
                bulkEventFirstCall,
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                phonePeResponse);

        addStubForPostRequest(
                url,
                headers,
                bulkEventSecondCall,
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                phonePeResponse);
        this.eventPublisher.run();

        // Second Call Made with Remaining Size ie 5 of Bulk Events
        BulkEvent actualSecondCall =
                this.objectMapper.readValue(
                        wireMockServer.getAllServeEvents().get(0).getRequest().getBody(),
                        BulkEvent.class);

        // First Call Made with BATCH_MAX_SIZE ie 10 of Bulk Events
        BulkEvent actualFirstCall =
                this.objectMapper.readValue(
                        wireMockServer.getAllServeEvents().get(1).getRequest().getBody(),
                        BulkEvent.class);

        Assertions.assertEquals(5, actualSecondCall.getEvents().size());
        Assertions.assertEquals(10, actualFirstCall.getEvents().size());
        wireMockServer.verify(2, postRequestedFor(urlPathMatching(url)));
        Assertions.assertEquals(0, this.eventQueue.size());
    }

    @Test
    void testPublishingEventsThroughThread() throws InterruptedException {
        wireMockServer.resetRequests();
        String apiPath = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .amount(amount)
                        .merchantOrderId(merchantOrderId)
                        .build();

        BaseEvent event =
                BaseEvent.buildStandardCheckoutPayEvent(
                        EventState.SUCCESS,
                        standardCheckoutPayRequest,
                        apiPath,
                        EventType.PAY_SUCCESS);

        List<BaseEvent> listOfEvents = Arrays.asList(event, event, event);
        for (BaseEvent singleEvent : listOfEvents) {
            this.eventPublisher.send(singleEvent);
        }

        String url = Constants.EVENTS_ENDPOINT;
        PhonePeResponse phonePeResponse =
                PhonePeResponse.builder().message("Success").code("SUCCESS").success(true).build();
        Map<String, String> headers = getEventHeaders();

        BulkEvent bulkEvent =
                BulkEvent.builder()
                        .events(listOfEvents)
                        .clientVersion(Headers.SDK_TYPE + ":" + Headers.SDK_VERSION)
                        .source(Constants.SOURCE)
                        .build();

        Assertions.assertEquals(3, this.eventQueue.size());

        addStubForPostRequest(
                url, headers, bulkEvent, HttpStatus.SC_OK, ImmutableMap.of(), phonePeResponse);

        eventPublisher.startPublishingEvents(tokenService::getAuthToken);
        Thread.sleep(2000);
        wireMockServer.verify(1, postRequestedFor(urlPathMatching(url)));
        Assertions.assertEquals(0, this.eventQueue.size());
    }

    @Test
    void testSendsTokenFetchFailureEvent() throws IOException, InterruptedException {
        TokenService.setOAuthResponse(null);
        wireMockServer.resetAll();
        tokenService =
                new TokenService(okHttpClient, objectMapper, credentialConfig, env, eventPublisher);
        String tokenExpireResponse =
                "{\"access_token\": \"expired_access_token\",\"encrypted_access_token\":"
                        + " \"encrypted_access_token\", \"refresh_token\": \"refresh_token\","
                        + " \"expires_in\": 0, \"issued_at\": 0, \"expires_at\":"
                        + " 0,\"session_expires_at\": 1709630316, \"token_type\": \"O-Bearer\"}";
        long currentTime = java.time.Instant.now().getEpochSecond();
        long currentTimeMore10 = currentTime + 10;
        String correctTokenResponse =
                "{\"access_token\": \"correct_access_token\",\"encrypted_access_token\":"
                        + " \"encrypted_access_token\", \"refresh_token\": \"refresh_token\","
                        + " \"expires_in\":"
                        + currentTimeMore10
                        + ", \"issued_at\":"
                        + currentTime
                        + ", "
                        + "\"expires_at\":"
                        + currentTimeMore10
                        + ",\"session_expires_at\": 1709630316, \"token_type\": \"O-Bearer\"}";

        String url = TokenConstants.OAUTH_GET_TOKEN;

        wireMockServer.stubFor(
                post(urlEqualTo(url))
                        .inScenario("Sequential Responses")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(aResponse().withStatus(200).withBody(tokenExpireResponse))
                        .willSetStateTo("Second Call"));
        tokenService.getAuthToken();

        wireMockServer.stubFor(
                post(urlEqualTo(url))
                        .inScenario("Sequential Responses")
                        .whenScenarioStateIs("Second Call")
                        .willReturn(aResponse().withStatus(500))
                        .willSetStateTo("Third Call"));

        wireMockServer.stubFor(
                post(urlEqualTo(url))
                        .inScenario("Sequential Responses")
                        .whenScenarioStateIs("Third Call")
                        .willReturn(aResponse().withStatus(200).withBody(correctTokenResponse)));

        String eventUrl = Constants.EVENTS_ENDPOINT;
        PhonePeResponse phonePeResponse =
                PhonePeResponse.builder().message("Success").code("SUCCESS").success(true).build();

        wireMockServer.stubFor(
                post(urlEqualTo(eventUrl))
                        .willReturn(
                                aResponse()
                                        .withBody(
                                                objectMapper.writeValueAsString(phonePeResponse))));

        this.eventPublisher.startPublishingEvents(tokenService::getAuthToken);
        Thread.sleep(5000);

        wireMockServer.verify(3, postRequestedFor(urlPathMatching(url)));
        wireMockServer.verify(2, postRequestedFor(urlPathMatching(eventUrl)));
        Assertions.assertEquals(0, this.eventQueue.size());
        Assertions.assertEquals("O-Bearer correct_access_token", tokenService.formatCachedToken());
    }

    public Map<String, String> getEventHeaders() {
        return ImmutableMap.<String, String>builder()
                .put(Headers.ACCEPT, APPLICATION_JSON)
                .put(Constants.AUTHORIZATION, tokenService.getAuthToken())
                .build();
    }
}
