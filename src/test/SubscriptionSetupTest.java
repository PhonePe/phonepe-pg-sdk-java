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
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class SubscriptionSetupTest extends BaseSetupWithOAuth {

    @Test
    void testSetupViaUpiIntentSuccess() {
        String url = SubscriptionConstants.SETUP_API;

        String merchantOrderId = "merchantOrderId";
        String merchantSubscriptionId = "merchantSubscriptionId";

        PgPaymentRequest setupRequest =
                PgPaymentRequest.SubscriptionSetupUpiIntentBuilder()
                        .merchantOrderId(merchantOrderId)
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .amount(200L)
                        .orderExpireAt(100L)
                        .subscriptionExpireAt(100L)
                        .frequency(Frequency.ON_DEMAND)
                        .build();

        PgPaymentResponse expected =
                PgPaymentResponse.builder().state("PENDING").orderId("OMOxxx").build();
        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(
                url, headers, setupRequest, HttpStatus.SC_OK, Maps.newHashMap(), expected);

        PgPaymentResponse actual = subscriptionClient.setup(setupRequest);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testSetupViaUpiCollectSuccess() {
        String url = SubscriptionConstants.SETUP_API;

        String merchantOrderId = "merchantOrderId";
        String merchantSubscriptionId = "merchantSubscriptionId";

        PgPaymentRequest setupRequest =
                PgPaymentRequest.SubscriptionSetupUpiCollectBuilder()
                        .merchantOrderId(merchantOrderId)
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .amount(200L)
                        .orderExpireAt(100L)
                        .subscriptionExpireAt(100L)
                        .frequency(Frequency.ON_DEMAND)
                        .build();

        PgPaymentResponse expected =
                PgPaymentResponse.builder().state("PENDING").orderId("OMOxxx").build();
        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(
                url, headers, setupRequest, HttpStatus.SC_OK, Maps.newHashMap(), expected);

        PgPaymentResponse actual = subscriptionClient.setup(setupRequest);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testSetupFailure() {
        String url = SubscriptionConstants.SETUP_API;

        String merchantOrderId = "merchantOrderId";
        String merchantSubscriptionId = "merchantSubscriptionId";

        PgPaymentRequest setupRequest =
                PgPaymentRequest.SubscriptionSetupUpiCollectBuilder()
                        .merchantOrderId(merchantOrderId)
                        .merchantSubscriptionId(merchantSubscriptionId)
                        .amount(200L)
                        .orderExpireAt(100L)
                        .subscriptionExpireAt(100L)
                        .frequency(Frequency.ON_DEMAND)
                        .build();

        PhonePeResponse phonePeResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("Bad Request")
                        .message("message")
                        .data(Collections.singletonMap("a", "b"))
                        .build();

        Map<String, String> headers = getHeadersForSubscription();

        addStubForPostRequest(
                url,
                headers,
                setupRequest,
                HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(),
                phonePeResponse);

        final PhonePeException phonePeException =
                assertThrows(PhonePeException.class, () -> subscriptionClient.setup(setupRequest));
        Assertions.assertEquals(400, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Bad Request", phonePeException.getCode());
        Assertions.assertEquals(true, phonePeException instanceof BadRequest);
    }
}
