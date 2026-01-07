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
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.CardPaymentModeConstraint;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.CardType;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.UpiIntentPaymentModeConstraint;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.request.PaymentModeConfig;
import com.phonepe.sdk.pg.payments.v2.models.request.PgCheckoutPaymentFlow;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class CreateOrderTest extends BaseSetupWithOAuth {

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

    @Test
    void testCreateOrderWithDisablePaymentRetryTrue() {
        String redirectUrl = "https://google.com";

        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.StandardCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .redirectUrl(redirectUrl)
                        .disablePaymentRetry(true)
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
        Assertions.assertTrue(createSdkOrderRequest.getDisablePaymentRetry());
    }

    @Test
    void testCreateOrderWithDisablePaymentRetryFalse() {
        String redirectUrl = "https://google.com";

        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.StandardCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .redirectUrl(redirectUrl)
                        .disablePaymentRetry(false)
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
        Assertions.assertFalse(createSdkOrderRequest.getDisablePaymentRetry());
    }

    @Test
    void testCreateOrderWithDisablePaymentRetryNull() {
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
        Assertions.assertNull(createSdkOrderRequest.getDisablePaymentRetry());
    }

    @Test
    void testCreateOrderCustomCheckoutWithDisablePaymentRetryTrue() {

        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.CustomCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .disablePaymentRetry(true)
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
        Assertions.assertTrue(createSdkOrderRequest.getDisablePaymentRetry());
    }

    @Test
    void testCreateOrderCustomCheckoutWithDisablePaymentRetryFalse() {

        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.CustomCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .disablePaymentRetry(false)
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
        Assertions.assertFalse(createSdkOrderRequest.getDisablePaymentRetry());
    }

    @Test
    void testCreateOrderCustomCheckoutWithDisablePaymentRetryNull() {

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
        Assertions.assertNull(createSdkOrderRequest.getDisablePaymentRetry());
    }

    @Test
    void testCreateOrderWithPaymentModeConfig() {
        String redirectUrl = "https://google.com";
        PaymentModeConfig paymentModeConfig =
                PaymentModeConfig.builder()
                        .enabledPaymentModes(List.of(
                                CardPaymentModeConstraint.builder().cardTypes(Set.of(CardType.CREDIT_CARD)).build(),
                                UpiIntentPaymentModeConstraint.builder().build()))
                        .build();

        CreateSdkOrderRequest createSdkOrderRequest =
                CreateSdkOrderRequest.StandardCheckoutBuilder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .redirectUrl(redirectUrl)
                        .paymentModeConfig(paymentModeConfig)
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
        PgCheckoutPaymentFlow pgCheckoutPaymentFlow = (PgCheckoutPaymentFlow)createSdkOrderRequest.getPaymentFlow();
        Assertions.assertNotNull(pgCheckoutPaymentFlow.getPaymentModeConfig());
        Assertions.assertEquals(
                paymentModeConfig.getEnabledPaymentModes(),
                pgCheckoutPaymentFlow.getPaymentModeConfig().getEnabledPaymentModes());
        Assertions.assertEquals(
                paymentModeConfig.getDisabledPaymentModes(),
                pgCheckoutPaymentFlow.getPaymentModeConfig().getDisabledPaymentModes());
    }

    @Test
    void testCreateOrderWithNullPaymentModeConfig() {
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
        PgCheckoutPaymentFlow pgCheckoutPaymentFlow = (PgCheckoutPaymentFlow)createSdkOrderRequest.getPaymentFlow();
        Assertions.assertNull(pgCheckoutPaymentFlow.getPaymentModeConfig());
    }
}
