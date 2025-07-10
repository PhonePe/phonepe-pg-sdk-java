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
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import java.util.Map;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class CancelSubscriptionTest extends BaseSetupWithOAuth {

    @Test
    void testCancelSubscription() {
        wireMockServer.resetRequests();
        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";
        String url = String.format(SubscriptionConstants.CANCEL_API, merchantSubscriptionId);

        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(url, headers, null, HttpStatus.SC_OK, Maps.newHashMap(), null);

        subscriptionClient.cancelSubscription(merchantSubscriptionId);

        wireMockServer.verify(exactly(1), postRequestedFor(urlPathMatching(url)));
    }

    @Test
    void testCancelSubscriptionIfResponseReceived() {
        wireMockServer.resetRequests();
        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";
        String url = String.format(SubscriptionConstants.CANCEL_API, merchantSubscriptionId);

        Map<String, String> headers = getHeadersForSubscription();

        String sampleJson = "{\"state\":\"PENDING\"}";

        addStubForPostRequest(url, headers, null, HttpStatus.SC_OK, Maps.newHashMap(), sampleJson);

        subscriptionClient.cancelSubscription(merchantSubscriptionId);

        wireMockServer.verify(exactly(1), postRequestedFor(urlPathMatching(url)));
    }
}
