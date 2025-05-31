import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableMap;
import com.phonepe.sdk.pg.common.exception.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import com.phonepe.sdk.pg.common.models.response.InstrumentCombo;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.common.models.response.PaymentDetail;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.AccountPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.CreditCardPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.EGVPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.NetBankingPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.WalletPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.rails.PgPaymentRail;
import com.phonepe.sdk.pg.common.models.response.rails.PpiEgvPaymentRail;
import com.phonepe.sdk.pg.common.models.response.rails.PpiWalletPaymentRail;
import com.phonepe.sdk.pg.common.models.response.rails.UpiPaymentRail;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import com.phonepe.sdk.pg.payments.v2.standardcheckout.StandardCheckoutConstants;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionConstants;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionSetupPaymentFlowResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

public class OrderStatusTest extends BaseSetupWithOAuth {

    @Test
    void orderStatusReturnSuccess() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        OrderStatusResponse orderStatusResponse = OrderStatusResponse.builder()
                .orderId("Order123")
                .state("PENDING")
                .build();

        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "false"),
                headers, HttpStatus.SC_OK, ImmutableMap.of(), orderStatusResponse);

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, false);
        Assertions.assertEquals(actual, orderStatusResponse);
    }

    @Test
    void orderStatusForSplitInstruments() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"COMPLETED\",\"amount\":300,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},"
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"UPI_INTENT\",\"timestamp\":1709108475,\"amount\":300,\"state\":\"FAILED\","
                + "\"errorCode\":\"PAYMENT_ERROR\",\"detailedErrorCode\":\"TXN_AUTO_FAILED\"},"
                + "{\"transactionId\":\"OM2402281351164694090252\",\"paymentMode\":\"PPE_INTENT\","
                + "\"timestamp\":1709108476,\"amount\":300,\"state\":\"COMPLETED\","
                + "\"splitInstruments\":[{\"instrument\":{\"type\":\"WALLET\"},\"rail\":{\"type\":\"PPI_WALLET\"},"
                + "\"amount\":150},{\"instrument\":{\"type\":\"EGV\"},\"rail\":{\"type\":\"PPI_EGV\"},"
                + "\"amount\":150}]}]}";
        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responeToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("COMPLETED")
                .amount(300)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402281351157894090178")
                        .paymentMode(PgV2InstrumentType.UPI_INTENT)
                        .timestamp(1709108475)
                        .amount(300)
                        .state("FAILED")
                        .errorCode("PAYMENT_ERROR")
                        .detailedErrorCode("TXN_AUTO_FAILED")
                        .build(), PaymentDetail.builder()
                        .transactionId("OM2402281351164694090252")
                        .paymentMode(PgV2InstrumentType.PPE_INTENT)
                        .timestamp(1709108476)
                        .amount(300)
                        .state("COMPLETED")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(WalletPaymentInstrumentV2.builder()
                                        .build())
                                .rail(PpiWalletPaymentRail.builder()
                                        .build())
                                .amount(150)
                                .build(), InstrumentCombo.builder()
                                .instrument(EGVPaymentInstrumentV2.builder()
                                        .build())
                                .rail(PpiEgvPaymentRail.builder()
                                        .build())
                                .amount(150)
                                .build()))
                        .build()))
                .build();

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responeToObject);
    }

    @Test
    void testTokenSuccess() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"COMPLETED\",\"amount\":300,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},"
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"TOKEN\",\"timestamp\":1709108475,\"amount\":300,\"state\":\"COMPLETED\","
                + "\"splitInstruments\":[{\"instrument\" : {\"type\":\"CREDIT_CARD\",\"bankTransactionId\":\"123\","
                + "\"bankId\":\"bankId\","
                + "\"brn\":\"brn\",\"arn\":\"arn\"}, \"rail\":{\"type\": \"PG\",\"transactionId\":\"123\","
                + "\"authorizationCode\":\"123\",\"serviceTransactionId\":\"123\"},\"amount\":300}]}]}";

        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responseToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("COMPLETED")
                .amount(300)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402281351157894090178")
                        .paymentMode(PgV2InstrumentType.TOKEN)
                        .timestamp(1709108475)
                        .amount(300)
                        .state("COMPLETED")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(CreditCardPaymentInstrumentV2.builder()
                                        .arn("arn")
                                        .brn("brn")
                                        .bankId("bankId")
                                        .bankTransactionId("123")
                                        .build())
                                .rail(PgPaymentRail.builder()
                                        .authorizationCode("123")
                                        .serviceTransactionId("123")
                                        .transactionId("123")
                                        .build())
                                .amount(300)
                                .build()))
                        .build()))
                .build();

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responseToObject);
    }

    @Test
    void testTokenFailed() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"FAILED\",\"amount\":300,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},\"errorCode\":\"AUTHORIZATION_ERROR\","
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"TOKEN\",\"timestamp\":1709108475,\"amount\":300,\"state\":\"FAILED\","
                + "\"errorCode\":\"AUTHORIZATION_ERROR\","
                + "\"splitInstruments\":[{\"instrument\" : {\"type\":\"CREDIT_CARD\",\"bankTransactionId\":\"123\","
                + "\"bankId\":\"bankId\","
                + "\"brn\":\"brn\",\"arn\":\"arn\"}, \"rail\":{\"type\": \"PG\",\"transactionId\":\"123\","
                + "\"authorizationCode\":\"123\",\"serviceTransactionId\":\"123\"},\"amount\":300}]}]}";
        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responeToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("FAILED")
                .amount(300)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .errorCode("AUTHORIZATION_ERROR")
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402281351157894090178")
                        .paymentMode(PgV2InstrumentType.TOKEN)
                        .timestamp(1709108475)
                        .amount(300)
                        .state("FAILED")
                        .errorCode("AUTHORIZATION_ERROR")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(CreditCardPaymentInstrumentV2.builder()
                                        .arn("arn")
                                        .brn("brn")
                                        .bankId("bankId")
                                        .bankTransactionId("123")
                                        .build())
                                .rail(PgPaymentRail.builder()
                                        .authorizationCode("123")
                                        .serviceTransactionId("123")
                                        .transactionId("123")
                                        .build())
                                .amount(300)
                                .build()))
                        .build()))
                .build();

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responeToObject);
    }

    @Test
    void testCardSuccess() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"COMPLETED\",\"amount\":300,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},"
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"CARD\",\"timestamp\":1709108475,\"amount\":300,\"state\":\"COMPLETED\","
                + "\"splitInstruments\":[{\"instrument\" : {\"type\":\"CREDIT_CARD\",\"bankTransactionId\":\"123\","
                + "\"bankId\":\"bankId\","
                + "\"brn\":\"brn\",\"arn\":\"arn\"}, \"rail\":{\"type\": \"PG\",\"transactionId\":\"123\","
                + "\"authorizationCode\":\"123\",\"serviceTransactionId\":\"123\"},\"amount\":300}]}]}";
        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responeToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("COMPLETED")
                .amount(300)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402281351157894090178")
                        .paymentMode(PgV2InstrumentType.CARD)
                        .timestamp(1709108475)
                        .amount(300)
                        .state("COMPLETED")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(CreditCardPaymentInstrumentV2.builder()
                                        .arn("arn")
                                        .brn("brn")
                                        .bankId("bankId")
                                        .bankTransactionId("123")
                                        .build())
                                .rail(PgPaymentRail.builder()
                                        .authorizationCode("123")
                                        .serviceTransactionId("123")
                                        .transactionId("123")
                                        .build())
                                .amount(300)
                                .build()))
                        .build()))
                .build();

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responeToObject);
    }

    @Test
    void testNetbankingSuccess() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"COMPLETED\",\"amount\":300,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},"
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"NET_BANKING\",\"timestamp\":1709108475,\"amount\":300,\"state\":\"COMPLETED\","
                + "\"splitInstruments\":[{\"instrument\" : {\"type\":\"NET_BANKING\",\"bankTransactionId\":\"123\","
                + "\"bankId\":\"bankId\","
                + "\"brn\":\"brn\",\"arn\":\"arn\"}, \"rail\":{\"type\": \"PG\",\"transactionId\":\"123\","
                + "\"authorizationCode\":\"123\",\"serviceTransactionId\":\"123\"},\"amount\":300}]}]}";
        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responeToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("COMPLETED")
                .amount(300)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402281351157894090178")
                        .paymentMode(PgV2InstrumentType.NET_BANKING)
                        .timestamp(1709108475)
                        .amount(300)
                        .state("COMPLETED")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(NetBankingPaymentInstrumentV2.builder()
                                        .arn("arn")
                                        .brn("brn")
                                        .bankId("bankId")
                                        .bankTransactionId("123")
                                        .build())
                                .rail(PgPaymentRail.builder()
                                        .authorizationCode("123")
                                        .serviceTransactionId("123")
                                        .transactionId("123")
                                        .build())
                                .amount(300)
                                .build()))
                        .build()))
                .build();

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responeToObject);
    }

    @Test
    void testOrderCheckStatusThrows404() {
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        Map<String, String> headers = getHeaders();
        PhonePeResponse phonePeResponse = PhonePeResponse.<Map<String, String>>builder()
                .code("code")
                .message("Not Found")
                .data(Collections.singletonMap("a", "b"))
                .build();
        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "false"),
                headers, HttpStatus.SC_NOT_FOUND, ImmutableMap.of(), phonePeResponse);

        final PhonePeException phonePeException = assertThrows(PhonePeException.class,
                () -> standardCheckoutClient.getOrderStatus(merchantOrderId, false));
        Assertions.assertEquals(404, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Not Found", phonePeException.getMessage());
    }

    @Test
    void testCheckStatusThrowsBadGateway() {
        final String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        PhonePeResponse phonePeResponse = PhonePeResponse.<Map<String, String>>builder()
                .code("code")
                .message("Bad Gateway")
                .data(Collections.singletonMap("a", "b"))
                .build();
        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "false"), getHeaders(),
                HttpStatus.SC_BAD_GATEWAY, ImmutableMap.of(), phonePeResponse);

        final PhonePeException phonePeException = assertThrows(PhonePeException.class,
                () -> standardCheckoutClient.getOrderStatus(merchantOrderId, false));
        Assertions.assertEquals(502, phonePeException.getHttpStatusCode());
        Assertions.assertEquals("Bad Gateway", phonePeException.getMessage());
    }

    @Test
    void testCustomCheckoutOrderStatus() {
        Map<String, String> headers = getHeaders();
        String url = String.format(CustomCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        OrderStatusResponse orderStatusResponse = OrderStatusResponse.builder()
                .orderId("Order123")
                .state("PENDING")
                .build();
        addStubForGetRequest(url, ImmutableMap.of(CustomCheckoutConstants.ORDER_DETAILS, "false"),
                headers, HttpStatus.SC_OK, ImmutableMap.of(), orderStatusResponse);
        OrderStatusResponse actual = customCheckoutClient.getOrderStatus(merchantOrderId, false);
        Assertions.assertEquals(actual, orderStatusResponse);
    }

    @Test
    void testPaymentDetails2Attempts() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);
        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"COMPLETED\",\"amount\":100,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},"
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"UPI_INTENT\",\"timestamp\":1709108475,\"amount\":100,\"state\":\"FAILED\","
                + "\"errorCode\" : \"PAYMENT_ERROR\", \"detailedErrorCode\": \"TXN_AUTO_FAILED\"},"
                + "{\"transactionId\":\"OM2402281351157894090179\","
                + "\"paymentMode\":\"NET_BANKING\",\"timestamp\":1709108475,\"amount\":100,\"state\":\"COMPLETED\","
                + "\"splitInstruments\":[{\"instrument\" : {\"type\":\"NET_BANKING\",\"bankTransactionId\":\"123\","
                + "\"bankId\":\"bankId\","
                + "\"brn\":\"brn\",\"arn\":\"arn\"}, \"rail\":{\"type\": \"PG\",\"transactionId\":\"123\","
                + "\"authorizationCode\":\"123\",\"serviceTransactionId\":\"123\"},\"amount\":100}]}]}";
        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responseToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("COMPLETED")
                .amount(100)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                                .transactionId("OM2402281351157894090178")
                                .paymentMode(PgV2InstrumentType.UPI_INTENT)
                                .timestamp(1709108475)
                                .amount(100)
                                .state("FAILED")
                                .errorCode("PAYMENT_ERROR")
                                .detailedErrorCode("TXN_AUTO_FAILED")
                                .build(),
                        PaymentDetail.builder()
                                .transactionId("OM2402281351157894090179")
                                .paymentMode(PgV2InstrumentType.NET_BANKING)
                                .timestamp(1709108475)
                                .amount(100)
                                .state("COMPLETED")
                                .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                        .instrument(NetBankingPaymentInstrumentV2.builder()
                                                .arn("arn")
                                                .brn("brn")
                                                .bankId("bankId")
                                                .bankTransactionId("123")
                                                .build())
                                        .rail(PgPaymentRail.builder()
                                                .authorizationCode("123")
                                                .serviceTransactionId("123")
                                                .transactionId("123")
                                                .build())
                                        .amount(100)
                                        .build()))
                                .build()))
                .build();
        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responseToObject);
    }

    @Test
    void testPpeIntent() {
        Map<String, String> headers = getHeaders();
        String url = String.format(StandardCheckoutConstants.ORDER_STATUS_API, merchantOrderId);

        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"COMPLETED\",\"amount\":300,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},"
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"PPE_INTENT\",\"timestamp\":1709108475,\"amount\":300,\"state\":\"COMPLETED\","
                + "\"splitInstruments\":[{\"instrument\" : {\"type\":\"WALLET\"}, \"rail\":{\"type\": \"PPI_WALLET\"},"
                + "\"amount\":300}]}]}";

        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responseToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("COMPLETED")
                .amount(300)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402281351157894090178")
                        .paymentMode(PgV2InstrumentType.PPE_INTENT)
                        .timestamp(1709108475)
                        .amount(300)
                        .state("COMPLETED")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(WalletPaymentInstrumentV2.builder()
                                        .build())
                                .rail(PpiWalletPaymentRail.builder()
                                        .build())
                                .amount(300)
                                .build()))
                        .build()))
                .build();

        OrderStatusResponse actual = standardCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responseToObject);
    }

    @Test
    void testPpeIntentCustom() {
        Map<String, String> headers = getHeaders();
        String url = String.format(CustomCheckoutConstants.ORDER_STATUS_API, merchantOrderId);

        String response = "{\"orderId\":\"OMO2402281351151884090310\",\"state\":\"COMPLETED\",\"amount\":300,"
                + "\"expireAt\":1709108893,\"metaInfo\":{\"udf1\":\"udf1\"},"
                + "\"paymentDetails\":[{\"transactionId\":\"OM2402281351157894090178\","
                + "\"paymentMode\":\"PPE_INTENT\",\"timestamp\":1709108475,\"amount\":300,\"state\":\"COMPLETED\","
                + "\"splitInstruments\":[{\"instrument\" : {\"type\":\"WALLET\"}, \"rail\":{\"type\": \"PPI_WALLET\"},"
                + "\"amount\":300}]}]}";

        addStubForGetRequest(url, ImmutableMap.of(StandardCheckoutConstants.ORDER_DETAILS, "true"), headers,
                HttpStatus.SC_OK,
                ImmutableMap.of(), response);

        OrderStatusResponse responseToObject = OrderStatusResponse.builder()
                .orderId("OMO2402281351151884090310")
                .state("COMPLETED")
                .amount(300)
                .expireAt(1709108893)
                .metaInfo(MetaInfo.builder()
                        .udf1("udf1")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402281351157894090178")
                        .paymentMode(PgV2InstrumentType.PPE_INTENT)
                        .timestamp(1709108475)
                        .amount(300)
                        .state("COMPLETED")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(WalletPaymentInstrumentV2.builder()
                                        .build())
                                .rail(PpiWalletPaymentRail.builder()
                                        .build())
                                .amount(300)
                                .build()))
                        .build()))
                .build();

        OrderStatusResponse actual = customCheckoutClient.getOrderStatus(merchantOrderId, true);
        Assertions.assertEquals(actual, responseToObject);
    }

    @Test
    void testSubscriptionOrderStatus() {
        String merchantOrderId = "OMOxxx";

        String url = String.format(SubscriptionConstants.ORDER_STATUS_API, merchantOrderId);

        String response = "{\"merchantId\": \"SWIGGY8\", \"merchantOrderId\": \"MO1708797962855\", \"orderId\": "
                + "\"OMO2402242336055135042802\", \"state\": \"COMPLETED\", \"amount\": 200, \"expireAt\": "
                + "170879838, \"paymentFlow\": {\"type\": \"SUBSCRIPTION_SETUP\", \"merchantSubscriptionId\": "
                + "\"MS1708797962855\", \"authWorkflowType\": \"TRANSACTION\", \"amountType\": \"FIXED\", "
                + "\"maxAmount\": 200, \"frequency\": \"ON_DEMAND\", \"subscriptionId\": "
                + "\"OMS2402242336054995042603\"}, \"paymentDetails\": [{\"transactionId\": "
                + "\"OM2402242336055865042862\", \"paymentMode\": \"UPI_INTENT\", \"timestamp\": 1708797965588, "
                + "\"amount\": 200, \"state\": \"COMPLETED\", \"splitInstruments\":[{\"instrument\": {\"type\": "
                + "\"ACCOUNT\", "
                + "\"maskedAccountNumber\": \"XXXXXXX20000\", \"ifsc\": \"AABE0000000\", \"accountHolderName\": "
                + "\"AABE0000000\", \"accountType\": \"SAVINGS\"}, \"rail\": {\"type\": \"UPI\", \"utr\": "
                + "\"405554491450\", \"vpa\": \"8668594479@ybl\"},\"amount\":200}],"
                + " \"errorCode\": \"PRESENT ONLY IF TRANSACTION IS FAILED\", \"detailedErrorCode\": \"PRESENT ONLY "
                + "IF TRANSACTION IS FAILED\"}]}";

        OrderStatusResponse responseObject = OrderStatusResponse.builder()
                .merchantId("SWIGGY8")
                .merchantOrderId("MO1708797962855")
                .orderId("OMO2402242336055135042802")
                .state("COMPLETED")
                .amount(200)
                .expireAt(170879838)
                .paymentFlow(SubscriptionSetupPaymentFlowResponse.builder()
                        .merchantSubscriptionId("MS1708797962855")
                        .authWorkflowType(AuthWorkflowType.TRANSACTION)
                        .amountType(AmountType.FIXED)
                        .maxAmount(200L)
                        .frequency(Frequency.ON_DEMAND)
                        .subscriptionId("OMS2402242336054995042603")
                        .build())
                .paymentDetails(Arrays.asList(PaymentDetail.builder()
                        .transactionId("OM2402242336055865042862")
                        .paymentMode(PgV2InstrumentType.UPI_INTENT)
                        .timestamp(1708797965588L)
                        .amount(200)
                        .state("COMPLETED")
                        .splitInstruments(Arrays.asList(InstrumentCombo.builder()
                                .instrument(AccountPaymentInstrumentV2.builder()
                                        .maskedAccountNumber("XXXXXXX20000")
                                        .ifsc("AABE0000000")
                                        .accountHolderName("AABE0000000")
                                        .accountType("SAVINGS")
                                        .build())
                                .rail(UpiPaymentRail.builder()
                                        .utr("405554491450")
                                        .vpa("8668594479@ybl")
                                        .build())
                                .amount(200)
                                .build()))
                        .errorCode("PRESENT ONLY IF TRANSACTION IS FAILED")
                        .detailedErrorCode("PRESENT ONLY IF TRANSACTION IS FAILED")
                        .build()))
                .build();

        Map<String, String> headers = getHeadersForSubscription();

        addStubForGetRequest(url, ImmutableMap.of(), headers, HttpStatus.SC_OK, ImmutableMap.of(), response);

        OrderStatusResponse actual = subscriptionClient.getOrderStatus(merchantOrderId);

        Assertions.assertEquals(responseObject, actual);
    }

}
