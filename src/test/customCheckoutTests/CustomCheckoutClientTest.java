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
package customCheckoutTests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.common.models.response.RefundResponse;
import com.phonepe.sdk.pg.common.models.response.RefundStatusResponse;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

class CustomCheckoutClientTest extends CustomCheckoutBaseSetup {

    // ───────────────────────────────────────────────────────────────────────
    // pay() – UPI Collect via VPA
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testPayViaVpaSuccess() {
        final String url = CustomCheckoutConstants.PAY_API;

        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_CC_VPA_001")
                        .amount(1000L)
                        .vpa("customer@upi")
                        .message("Payment for order")
                        .build();

        PgPaymentResponse response =
                PgPaymentResponse.builder()
                        .orderId("OMO001")
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond() + 600)
                        .build();

        addStubForPostRequest(
                url, getHeaders(), request, HttpStatus.SC_OK, Maps.newHashMap(), response);

        PgPaymentResponse actual = customCheckoutClient.pay(request);
        Assertions.assertEquals(response, actual);
    }

    @Test
    void testPayViaVpaBadRequest() {
        final String url = CustomCheckoutConstants.PAY_API;

        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_CC_VPA_002")
                        .amount(500L)
                        .vpa("invalid-vpa")
                        .build();

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("BAD_REQUEST")
                        .message("Invalid VPA")
                        .data(Collections.singletonMap("field", (Object)"vpa"))
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                request,
                HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(),
                errorResponse);

        PhonePeException exception =
                assertThrows(PhonePeException.class, () -> customCheckoutClient.pay(request));
        Assertions.assertEquals(400, exception.getHttpStatusCode());
        Assertions.assertEquals("BAD_REQUEST", exception.getCode());
    }

    // ───────────────────────────────────────────────────────────────────────
    // pay() – UPI Collect via Phone Number
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testPayViaPhoneNumberSuccess() {
        final String url = CustomCheckoutConstants.PAY_API;

        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_CC_PHONE_001")
                        .amount(2000L)
                        .phoneNumber("9876543210")
                        .message("Pay for subscription")
                        .build();

        PgPaymentResponse response =
                PgPaymentResponse.builder()
                        .orderId("OMO002")
                        .state("PENDING")
                        .expireAt(java.time.Instant.now().getEpochSecond() + 600)
                        .build();

        addStubForPostRequest(
                url, getHeaders(), request, HttpStatus.SC_OK, Maps.newHashMap(), response);

        PgPaymentResponse actual = customCheckoutClient.pay(request);
        Assertions.assertEquals(response, actual);
    }

    @Test
    void testPayViaPhoneNumberBadRequest() {
        final String url = CustomCheckoutConstants.PAY_API;

        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_CC_PHONE_002")
                        .amount(500L)
                        .phoneNumber("000")
                        .build();

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("INVALID_PHONE_NUMBER")
                        .message("Phone number is not valid")
                        .data(Collections.emptyMap())
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                request,
                HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(),
                errorResponse);

        PhonePeException exception =
                assertThrows(PhonePeException.class, () -> customCheckoutClient.pay(request));
        Assertions.assertEquals(400, exception.getHttpStatusCode());
        Assertions.assertEquals("INVALID_PHONE_NUMBER", exception.getCode());
    }

    @Test
    void testPayServerError() {
        final String url = CustomCheckoutConstants.PAY_API;

        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_CC_VPA_SERVER_ERR")
                        .amount(1500L)
                        .vpa("user@upi")
                        .build();

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("INTERNAL_SERVER_ERROR")
                        .message("Service Unavailable")
                        .data(Collections.emptyMap())
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                request,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                Maps.newHashMap(),
                errorResponse);

        PhonePeException exception =
                assertThrows(PhonePeException.class, () -> customCheckoutClient.pay(request));
        Assertions.assertEquals(500, exception.getHttpStatusCode());
    }

    // ───────────────────────────────────────────────────────────────────────
    // getOrderStatus()
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testGetOrderStatusSuccess() {
        String url =
                String.format(CustomCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        OrderStatusResponse orderStatusResponse =
                OrderStatusResponse.builder()
                        .orderId("OMO003")
                        .merchantOrderId(merchantOrderId)
                        .state("COMPLETED")
                        .amount(1000L)
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(CustomCheckoutConstants.ORDER_DETAILS, "false"),
                getHeaders(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                orderStatusResponse);

        OrderStatusResponse actual = customCheckoutClient.getOrderStatus(merchantOrderId);
        Assertions.assertEquals(orderStatusResponse, actual);
    }

    @Test
    void testGetOrderStatusWithDetailsFlagSuccess() {
        String url =
                String.format(CustomCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        OrderStatusResponse orderStatusResponse =
                OrderStatusResponse.builder()
                        .orderId("OMO004")
                        .merchantOrderId(merchantOrderId)
                        .state("PENDING")
                        .amount(2000L)
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(CustomCheckoutConstants.ORDER_DETAILS, "true"),
                getHeaders(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                orderStatusResponse);

        OrderStatusResponse actual = customCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(orderStatusResponse, actual);
    }

    @Test
    void testGetOrderStatusNotFound() {
        String unknownOrderId = "UNKNOWN_ORDER";
        String url = String.format(CustomCheckoutConstants.ORDER_STATUS_API, unknownOrderId);

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("ORDER_NOT_FOUND")
                        .message("Order not found")
                        .data(Collections.emptyMap())
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(CustomCheckoutConstants.ORDER_DETAILS, "false"),
                getHeaders(),
                HttpStatus.SC_NOT_FOUND,
                ImmutableMap.of(),
                errorResponse);

        PhonePeException exception =
                assertThrows(
                        PhonePeException.class,
                        () -> customCheckoutClient.getOrderStatus(unknownOrderId));
        Assertions.assertEquals(404, exception.getHttpStatusCode());
        Assertions.assertEquals("ORDER_NOT_FOUND", exception.getCode());
    }

    // ───────────────────────────────────────────────────────────────────────
    // getTransactionStatus()
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testGetTransactionStatusSuccess() {
        String transactionId = "TXN_CC_001";
        String url = String.format(CustomCheckoutConstants.TRANSACTION_STATUS_API, transactionId);

        OrderStatusResponse transactionStatusResponse =
                OrderStatusResponse.builder()
                        .orderId("OMO005")
                        .state("COMPLETED")
                        .amount(3000L)
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(),
                getHeaders(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                transactionStatusResponse);

        OrderStatusResponse actual = customCheckoutClient.getTransactionStatus(transactionId);
        Assertions.assertEquals(transactionStatusResponse, actual);
    }

    @Test
    void testGetTransactionStatusNotFound() {
        String transactionId = "TXN_INVALID";
        String url = String.format(CustomCheckoutConstants.TRANSACTION_STATUS_API, transactionId);

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("TRANSACTION_NOT_FOUND")
                        .message("Transaction does not exist")
                        .data(Collections.emptyMap())
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(),
                getHeaders(),
                HttpStatus.SC_NOT_FOUND,
                ImmutableMap.of(),
                errorResponse);

        PhonePeException exception =
                assertThrows(
                        PhonePeException.class,
                        () -> customCheckoutClient.getTransactionStatus(transactionId));
        Assertions.assertEquals(404, exception.getHttpStatusCode());
        Assertions.assertEquals("TRANSACTION_NOT_FOUND", exception.getCode());
    }

    // ───────────────────────────────────────────────────────────────────────
    // refund()
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testRefundSuccess() {
        final String url = CustomCheckoutConstants.REFUND_API;

        RefundRequest refundRequest =
                RefundRequest.builder()
                        .merchantRefundId("REFUND_CC_001")
                        .originalMerchantOrderId(merchantOrderId)
                        .amount(500L)
                        .build();

        RefundResponse refundResponse =
                RefundResponse.builder()
                        .refundId("REFUND_CC_001")
                        .state("CREATED")
                        .amount(500L)
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                refundRequest,
                HttpStatus.SC_OK,
                Maps.newHashMap(),
                refundResponse);

        RefundResponse actual = customCheckoutClient.refund(refundRequest);
        Assertions.assertEquals(refundResponse, actual);
    }

    @Test
    void testRefundBadRequest() {
        final String url = CustomCheckoutConstants.REFUND_API;

        RefundRequest refundRequest =
                RefundRequest.builder()
                        .merchantRefundId("REFUND_CC_002")
                        .originalMerchantOrderId("NONEXISTENT_ORDER")
                        .amount(9999999L)
                        .build();

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("REFUND_AMOUNT_EXCEEDS")
                        .message("Refund amount exceeds original order amount")
                        .data(Collections.emptyMap())
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                refundRequest,
                HttpStatus.SC_BAD_REQUEST,
                Maps.newHashMap(),
                errorResponse);

        PhonePeException exception =
                assertThrows(
                        PhonePeException.class, () -> customCheckoutClient.refund(refundRequest));
        Assertions.assertEquals(400, exception.getHttpStatusCode());
        Assertions.assertEquals("REFUND_AMOUNT_EXCEEDS", exception.getCode());
    }

    // ───────────────────────────────────────────────────────────────────────
    // getRefundStatus()
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testGetRefundStatusSuccess() {
        String refundId = "REFUND_STATUS_CC_001";
        String url = String.format(CustomCheckoutConstants.REFUND_STATUS_API, refundId);

        RefundStatusResponse refundStatusResponse =
                RefundStatusResponse.builder()
                        .merchantRefundId(refundId)
                        .originalMerchantOrderId(merchantOrderId)
                        .amount(500L)
                        .state("COMPLETED")
                        .paymentDetails(Collections.emptyList())
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(),
                getHeaders(),
                HttpStatus.SC_OK,
                ImmutableMap.of(),
                refundStatusResponse);

        RefundStatusResponse actual = customCheckoutClient.getRefundStatus(refundId);
        Assertions.assertEquals(refundStatusResponse, actual);
    }

    @Test
    void testGetRefundStatusNotFound() {
        String refundId = "REFUND_UNKNOWN";
        String url = String.format(CustomCheckoutConstants.REFUND_STATUS_API, refundId);

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("REFUND_NOT_FOUND")
                        .message("Refund does not exist")
                        .data(Collections.emptyMap())
                        .build();

        addStubForGetRequest(
                url,
                ImmutableMap.of(),
                getHeaders(),
                HttpStatus.SC_NOT_FOUND,
                ImmutableMap.of(),
                errorResponse);

        PhonePeException exception =
                assertThrows(
                        PhonePeException.class,
                        () -> customCheckoutClient.getRefundStatus(refundId));
        Assertions.assertEquals(404, exception.getHttpStatusCode());
        Assertions.assertEquals("REFUND_NOT_FOUND", exception.getCode());
    }

    // ───────────────────────────────────────────────────────────────────────
    // createSdkOrder()
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testCreateSdkOrderSuccess() {
        final String url = CustomCheckoutConstants.CREATE_ORDER_API;

        com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest sdkRequest =
                com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest
                        .CustomCheckoutBuilder()
                        .merchantOrderId("SDK_ORDER_001")
                        .amount(2000L)
                        .build();

        com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse sdkResponse =
                com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse.builder()
                        .orderId("OMO_SDK_001")
                        .state("PENDING")
                        .token("sdk-token-abc")
                        .expireAt(java.time.Instant.now().getEpochSecond() + 600)
                        .build();

        addStubForPostRequest(
                url, getHeaders(), sdkRequest, HttpStatus.SC_OK, Maps.newHashMap(), sdkResponse);

        com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse actual =
                customCheckoutClient.createSdkOrder(sdkRequest);
        Assertions.assertEquals(sdkResponse.getOrderId(), actual.getOrderId());
        Assertions.assertEquals(sdkResponse.getToken(), actual.getToken());
    }

    @Test
    void testCreateSdkOrderFailure() {
        final String url = CustomCheckoutConstants.CREATE_ORDER_API;

        com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest sdkRequest =
                com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest
                        .CustomCheckoutBuilder()
                        .merchantOrderId("SDK_ORDER_FAIL_001")
                        .amount(2000L)
                        .build();

        PhonePeResponse errorResponse =
                PhonePeResponse.<Map<String, String>>builder()
                        .code("INTERNAL_SERVER_ERROR")
                        .message("Something went wrong")
                        .data(Collections.emptyMap())
                        .build();

        addStubForPostRequest(
                url,
                getHeaders(),
                sdkRequest,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                Maps.newHashMap(),
                errorResponse);

        PhonePeException exception =
                assertThrows(
                        PhonePeException.class,
                        () -> customCheckoutClient.createSdkOrder(sdkRequest));
        Assertions.assertEquals(500, exception.getHttpStatusCode());
    }

    // ───────────────────────────────────────────────────────────────────────
    // validateCallback()
    // ───────────────────────────────────────────────────────────────────────

    @Test
    void testValidateCallbackMalformedJsonThrows() {
        // sha256("username" + "password") = bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e
        assertThrows(
                Exception.class,
                () -> customCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        "{not valid json"));
    }
}
