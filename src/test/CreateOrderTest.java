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
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class CreateOrderTest extends BaseSetupWithOAuth {

    @Test
    void testCreateOrder() {
        String redirectUrl = "https://google.com";

        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.StandardCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .redirectUrl(redirectUrl)
                        .build();
        final String url = StandardCheckoutConstants.CREATE_ORDER_API;
        CreateSdkOrderResponse createSdkOrderResponse =
                CreateSdkOrderResponse.builder()
                        .expireAt(1432423)
                        .orderId("orderId")
                        .token("token")
                        .state("PENDING")
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                createSdkOrderRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                createSdkOrderResponse);

        CreateSdkOrderResponse actual =
                standardCheckoutClient.createSdkOrder(createSdkOrderRequest);
        Assertions.assertEquals(actual, createSdkOrderResponse);
    }

    @Test
    void testCreateOrderCustomCheckout() {

        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.CustomCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .build();
        final String url = CustomCheckoutConstants.CREATE_ORDER_API;
        CreateSdkOrderResponse createSdkOrderResponse =
                CreateSdkOrderResponse.builder()
                        .expireAt(1432423)
                        .orderId("orderId")
                        .token("token")
                        .state("PENDING")
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                createSdkOrderRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                createSdkOrderResponse);

        CreateSdkOrderResponse actual = customCheckoutClient.createSdkOrder(createSdkOrderRequest);
        Assertions.assertEquals(actual, createSdkOrderResponse);
    }

    @Test
    void testCreateOrderBadRequest() {
        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.StandardCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .build();

        PhonePeResponse phonePeResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("Bad Request")
                        .message("message")
                        .data(Collections.singletonMap("a", "b"))
                        .build();
        final String url = StandardCheckoutConstants.CREATE_ORDER_API;

        addStubForPostRequest(
                url,
                getHeaders(),
                createSdkOrderRequest,
                HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(),
                phonePeResponse);

        final PhonePeException phonePeException =
                assertThrows(
                        PhonePeException.class,
                        () -> standardCheckoutClient.createSdkOrder(createSdkOrderRequest));

        Assertions.assertEquals(400, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Bad Request", phonePeException.getCode());
    }
}
