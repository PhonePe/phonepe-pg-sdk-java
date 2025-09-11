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
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.exception.BadRequest;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import com.phonepe.sdk.pg.subscription.v2.models.request.RedemptionRetryStrategy;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class SubscriptionNotifyTest extends BaseSetupWithOAuth {

    @Test
    void testNotifySuccess() {
        String merchantOrderId = UUID.randomUUID().toString();

        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";
        RedemptionRetryStrategy redemptionRetryStrategy = RedemptionRetryStrategy.STANDARD;

        String url = SubscriptionConstants.NOTIFY_API;

        PgPaymentRequest notifyRequest =
                PgPaymentRequest.SubscriptionNotifyRequestBuilder()
                        .merchantOrderId(merchantOrderId)
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .redemptionRetryStrategy(redemptionRetryStrategy)
                        .build();

        PgPaymentResponse response =
                PgPaymentResponse.builder().state("PENDING").orderId("OMOxxx").build();

        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(
                url, headers, notifyRequest, HttpStatus.SC_OK, Maps.newHashMap(), response);

        PgPaymentResponse actual = subscriptionClient.notify(notifyRequest);

        Assertions.assertEquals(response, actual);
    }

    @Test
    void testNotifyFailed() {
        String merchantOrderId = UUID.randomUUID().toString();

        String merchantSubscriptionId = "c61d7378-a081-44ab-9559-ad8563a24b49";
        RedemptionRetryStrategy redemptionRetryStrategy = RedemptionRetryStrategy.STANDARD;

        String url = SubscriptionConstants.NOTIFY_API;

        PgPaymentRequest notifyRequest =
                PgPaymentRequest.SubscriptionNotifyRequestBuilder()
                        .merchantOrderId(merchantOrderId)
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .redemptionRetryStrategy(redemptionRetryStrategy)
                        .build();

        PhonePeResponse response =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("Bad Request")
                        .message("message")
                        .data(Collections.singletonMap("a", "b"))
                        .build();

        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(
                url,
                headers,
                notifyRequest,
                HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(),
                response);

        final PhonePeException actual =
                assertThrows(
                        PhonePeException.class, () -> subscriptionClient.notify(notifyRequest));

        Assertions.assertEquals(400, actual.getHttpStatusCode());
        Assertions.assertEquals("Bad Request", actual.getCode());
        Assertions.assertTrue(actual instanceof BadRequest);
    }
}
