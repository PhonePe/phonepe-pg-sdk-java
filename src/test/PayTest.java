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
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.CardPaymentModeConstraint;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.CardType;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.NetBankingPaymentModeConstraint;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.PaymentModeConstraint;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.UpiCollectPaymentModeConstraint;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.UpiIntentPaymentModeConstraint;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.UpiQrPaymentModeConstraint;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import com.phonepe.sdk.pg.payments.v2.models.request.PaymentModeConfig;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class PayTest extends BaseSetupWithOAuth {

    @SneakyThrows
    @Test
    void testPayReturnSuccess() {
        final String url = StandardCheckoutConstants.PAY_API;
        String redirectUrl = "https://redirectUrl.com";

        List<PaymentModeConstraint> enabledPaymentModes = new ArrayList<>();
        Set<CardType> allowedCardTypes = new HashSet<>();
        allowedCardTypes.add(CardType.DEBIT_CARD);
        allowedCardTypes.add(CardType.CREDIT_CARD);

        PaymentModeConstraint cardPaymentModeConstraint = CardPaymentModeConstraint.builder()
                .cardTypes(allowedCardTypes)
                .build();
        PaymentModeConstraint netBankingPaymentModeConstraint = NetBankingPaymentModeConstraint
                .builder()
                .build();
        PaymentModeConstraint upiIntentPaymentModeConstraint = UpiIntentPaymentModeConstraint
                .builder()
                .build();
        PaymentModeConstraint upiCollectPaymentModeConstraint = UpiCollectPaymentModeConstraint
                .builder()
                .build();
        PaymentModeConstraint upiQrPaymentModeConstraint = UpiQrPaymentModeConstraint
                .builder()
                .build();

        enabledPaymentModes.add(cardPaymentModeConstraint);
        enabledPaymentModes.add(netBankingPaymentModeConstraint);
        enabledPaymentModes.add(upiIntentPaymentModeConstraint);
        enabledPaymentModes.add(upiCollectPaymentModeConstraint);
        enabledPaymentModes.add(upiQrPaymentModeConstraint);

        PaymentModeConfig paymentModeConfig =
                PaymentModeConfig.builder()
                        .enabledPaymentModes(enabledPaymentModes)
                        .disabledPaymentModes(Collections.emptyList())
                        .build();
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .redirectUrl(redirectUrl)
                        .paymentModeConfig(paymentModeConfig)
                        .build();
        StandardCheckoutPayResponse standardCheckoutResponse =
                StandardCheckoutPayResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("https://google.com")
                        .build();
        Map<String, String> headers = getHeaders();

        addStubForPostRequest(
                url,
                headers,
                standardCheckoutPayRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                standardCheckoutResponse);
        StandardCheckoutPayResponse actual = standardCheckoutClient.pay(standardCheckoutPayRequest);
        Assertions.assertEquals(standardCheckoutResponse, actual);
    }

    @Test
    void testPayBadRequest() {
        final String url = StandardCheckoutConstants.PAY_API;
        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .build();
        PhonePeResponse phonePeResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("Bad Request")
                        .message("message")
                        .data(Collections.singletonMap("a", "b"))
                        .build();
        addStubForPostRequest(
                url,
                getHeaders(),
                standardCheckoutPayRequest,
                HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(),
                phonePeResponse);

        final PhonePeException phonePeException =
                assertThrows(
                        PhonePeException.class,
                        () -> standardCheckoutClient.pay(standardCheckoutPayRequest));
        Assertions.assertEquals(400, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Bad Request", phonePeException.getCode());
    }

    @Test
    void testCustomPay() {
        final String url = CustomCheckoutConstants.PAY_API;

        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("MerchantOrderId")
                        .amount(100)
                        .build();
        PgPaymentResponse pgPaymentResponse =
                PgPaymentResponse.builder()
                        .orderId("OMO2403071446458436434329")
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("mercury.com")
                        .build();
        addStubForPostRequest(
                url, getHeaders(), request, HttpStatus.SC_OK, Maps.newHashMap(), pgPaymentResponse);

        PgPaymentResponse actual = customCheckoutClient.pay(request);
        Assertions.assertEquals(pgPaymentResponse, actual);
    }

    @SneakyThrows
    @Test
    void testStandardCheckoutPayWithDisablePaymentRetryTrue() {
        final String url = StandardCheckoutConstants.PAY_API;
        String redirectUrl = "https://redirectUrl.com";

        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .disablePaymentRetry(true)
                        .redirectUrl(redirectUrl)
                        .build();
        StandardCheckoutPayResponse standardCheckoutResponse =
                StandardCheckoutPayResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("https://google.com")
                        .build();
        Map<String, String> headers = getHeaders();

        addStubForPostRequest(
                url,
                headers,
                standardCheckoutPayRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                standardCheckoutResponse);
        StandardCheckoutPayResponse actual = standardCheckoutClient.pay(standardCheckoutPayRequest);
        Assertions.assertEquals(standardCheckoutResponse, actual);
        Assertions.assertTrue(standardCheckoutPayRequest.getDisablePaymentRetry());
    }

    @SneakyThrows
    @Test
    void testStandardCheckoutPayWithDisablePaymentRetryFalse() {
        final String url = StandardCheckoutConstants.PAY_API;
        String redirectUrl = "https://redirectUrl.com";

        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .disablePaymentRetry(false)
                        .redirectUrl(redirectUrl)
                        .build();
        StandardCheckoutPayResponse standardCheckoutResponse =
                StandardCheckoutPayResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("https://google.com")
                        .build();
        Map<String, String> headers = getHeaders();

        addStubForPostRequest(
                url,
                headers,
                standardCheckoutPayRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                standardCheckoutResponse);
        StandardCheckoutPayResponse actual = standardCheckoutClient.pay(standardCheckoutPayRequest);
        Assertions.assertEquals(standardCheckoutResponse, actual);
        Assertions.assertFalse(standardCheckoutPayRequest.getDisablePaymentRetry());
    }

    @SneakyThrows
    @Test
    void testStandardCheckoutPayWithDisablePaymentRetryNull() {
        final String url = StandardCheckoutConstants.PAY_API;
        String redirectUrl = "https://redirectUrl.com";

        StandardCheckoutPayRequest standardCheckoutPayRequest =
                StandardCheckoutPayRequest.builder()
                        .merchantOrderId(merchantOrderId)
                        .amount(amount)
                        .redirectUrl(redirectUrl)
                        .build();
        StandardCheckoutPayResponse standardCheckoutResponse =
                StandardCheckoutPayResponse.builder()
                        .orderId(String.valueOf(java.time.Instant.now().getEpochSecond()))
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond())
                        .redirectUrl("https://google.com")
                        .build();
        Map<String, String> headers = getHeaders();

        addStubForPostRequest(
                url,
                headers,
                standardCheckoutPayRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                standardCheckoutResponse);
        StandardCheckoutPayResponse actual = standardCheckoutClient.pay(standardCheckoutPayRequest);
        Assertions.assertEquals(standardCheckoutResponse, actual);
        Assertions.assertNull(standardCheckoutPayRequest.getDisablePaymentRetry());
    }
}
