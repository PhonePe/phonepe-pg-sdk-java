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
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import com.phonepe.sdk.pg.common.models.response.CallbackData;
import com.phonepe.sdk.pg.common.models.response.CallbackResponse;
import com.phonepe.sdk.pg.common.models.response.InstrumentCombo;
import com.phonepe.sdk.pg.common.models.response.PaymentDetail;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.AccountPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.CreditCardPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.DebitCardPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.EGVPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.NetBankingPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.WalletPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.rails.PgPaymentRail;
import com.phonepe.sdk.pg.common.models.response.rails.PpiEgvPaymentRail;
import com.phonepe.sdk.pg.common.models.response.rails.PpiWalletPaymentRail;
import com.phonepe.sdk.pg.common.models.response.rails.UpiPaymentRail;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import com.phonepe.sdk.pg.subscription.v2.models.request.RedemptionRetryStrategy;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionRedemptionPaymentFlowResponse;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionSetupPaymentFlowResponse;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CallbackTest extends BaseSetup {

    @Test
    void test_ENTIRE_PG_ORDER_COMPLETED() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"UPI\","
                    + "\"upiTransactionId\":\"upi12313\",\"vpa\":\"abcd@ybl\"},"
                    + "\"instrument\":{\"type\":\"ACCOUNT\","
                    + "\"accountType\":\"SAVINGS\",\"maskedAccountNumber\":\"121212121212\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .instrument(
                                                                                                AccountPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .accountType(
                                                                                                                "SAVINGS")
                                                                                                        .maskedAccountNumber(
                                                                                                                "121212121212")
                                                                                                        .build())
                                                                                        .rail(
                                                                                                UpiPaymentRail
                                                                                                        .builder()
                                                                                                        .upiTransactionId(
                                                                                                                "upi12313")
                                                                                                        .vpa(
                                                                                                                "abcd@ybl")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCHECKOUT_TRANSACTION_ATTEMPTED_FAILED() {
        String jsonString =
                "{\"event\":\"checkout.transaction.attempt.failed\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse callbackResponse =
                CallbackResponse.builder()
                        .event("checkout.transaction.attempt.failed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();

        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);

        Assertions.assertEquals(callbackResponse, actual);
    }

    @Test
    void testPG_ORDER_FAILED() {
        String jsonString =
                "{\"event\":\"pg.order.failed\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.failed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testPG_TRANSACTION_ATTEMPT_FAILED() {
        String jsonString =
                "{\"event\":\"pg.transaction.attempt.failed\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.transaction.attempt.failed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testPG_REFUND_FAILED() {
        String jsonString =
                "{\"event\":\"pg.refund.failed\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.refund.failed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testPG_REFUND_COMPLETED() {
        String jsonString =
                "{\"event\":\"pg.refund.completed\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.refund.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testPG_REFUND_ACCEPTED() {
        String jsonString =
                "{\"event\":\"pg.refund.accepted\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.refund.accepted")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCHECKOUT_ORDER_COMPLETED() {
        String jsonString =
                "{\"event\":\"checkout.order.completed\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("checkout.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCHECKOUT_ORDER_FAILED() {
        String jsonString =
                "{\"event\":\"checkout.order.failed\",\"payload\":"
                        + "{\"orderId\":\"OMOxx\",\"paymentDetails\":"
                        + "[{\"paymentMode\":\"UPI_COLLECT\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("checkout.order.failed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRailPgRail() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PG\","
                    + "\"authorizationCode\":\"authorization\",\"serviceTransactionId\":\"serviceTransactionId\","
                    + "\"transactionId\":\"transactionId\"},"
                    + "\"instrument\":{\"type\":\"ACCOUNT\",\"accountType\":\"SAVINGS\","
                    + "\"maskedAccountNumber\":\"121212121212\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .instrument(
                                                                                                AccountPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .accountType(
                                                                                                                "SAVINGS")
                                                                                                        .maskedAccountNumber(
                                                                                                                "121212121212")
                                                                                                        .build())
                                                                                        .rail(
                                                                                                PgPaymentRail
                                                                                                        .builder()
                                                                                                        .authorizationCode(
                                                                                                                "authorization")
                                                                                                        .serviceTransactionId(
                                                                                                                "serviceTransactionId")
                                                                                                        .transactionId(
                                                                                                                "transactionId")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRailPpiWallet() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_WALLET\"},"
                    + "\"instrument\":{\"type\":\"ACCOUNT\",\"accountType\":\"SAVINGS\","
                    + "\"maskedAccountNumber\":\"121212121212\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiWalletPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                AccountPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .accountType(
                                                                                                                "SAVINGS")
                                                                                                        .maskedAccountNumber(
                                                                                                                "121212121212")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRailPpiEgv() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_EGV\"},"
                    + "\"instrument\":{\"type\":\"ACCOUNT\",\"accountType\":\"SAVINGS\","
                    + "\"maskedAccountNumber\":\"121212121212\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiEgvPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                AccountPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .accountType(
                                                                                                                "SAVINGS")
                                                                                                        .maskedAccountNumber(
                                                                                                                "121212121212")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testInstrumentWallet() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_EGV\"},"
                    + "\"instrument\":{\"type\":\"WALLET\",\"walletId\":\"walletId\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiEgvPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                WalletPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .walletId(
                                                                                                                "walletId")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testInstrumentEgv() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_EGV\"},"
                    + "\"instrument\":{\"type\":\"EGV\",\"cardNumber\":\"cardNumber\","
                    + "\"programId\":\"programId\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiEgvPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                EGVPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .cardNumber(
                                                                                                                "cardNumber")
                                                                                                        .programId(
                                                                                                                "programId")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testInstrumentCreditCard() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_EGV\"},"
                    + "\"instrument\":{\"type\":\"CREDIT_CARD\",\"bankTransactionId\":\"bankTransactionId\","
                    + "\"bankId\":\"bankId\",\"brn\":\"brn\",\"arn\":\"arn\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiEgvPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                CreditCardPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .arn(
                                                                                                                "arn")
                                                                                                        .brn(
                                                                                                                "brn")
                                                                                                        .bankId(
                                                                                                                "bankId")
                                                                                                        .bankTransactionId(
                                                                                                                "bankTransactionId")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testInstrumentDebitCard() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_EGV\"},"
                    + "\"instrument\":{\"type\":\"DEBIT_CARD\",\"bankTransactionId\":\"bankTransactionId\","
                    + "\"bankId\":\"bankId\",\"brn\":\"brn\",\"arn\":\"arn\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiEgvPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                DebitCardPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .arn(
                                                                                                                "arn")
                                                                                                        .brn(
                                                                                                                "brn")
                                                                                                        .bankId(
                                                                                                                "bankId")
                                                                                                        .bankTransactionId(
                                                                                                                "bankTransactionId")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testInstrumentNetbanking() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_EGV\"},"
                    + "\"instrument\":{\"type\":\"NET_BANKING\",\"bankTransactionId\":\"bankTransactionId\","
                    + "\"bankId\":\"bankId\",\"brn\":\"brn\",\"arn\":\"arn\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiEgvPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                NetBankingPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .arn(
                                                                                                                "arn")
                                                                                                        .brn(
                                                                                                                "brn")
                                                                                                        .bankId(
                                                                                                                "bankId")
                                                                                                        .bankTransactionId(
                                                                                                                "bankTransactionId")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                standardCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void validateCallbackCustom() {
        String jsonString =
                "{\"event\":\"pg.order.completed\",\"payload\":"
                    + "{\"orderId\":\"OMOxx\",\"merchantId\":\"merchantId\",\"merchantOrderId\":"
                    + "\"merchantOrderId\",\"state\":\"EXPIRED\",\"amount\":10000,\"expireAt\":1291391291,\"metaInfo\":"
                    + "{\"udf1\":\"\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\"},\"paymentDetails\":"
                    + "[{\"paymentMode\":\"UPI_COLLECT\",\"timestamp\":12121212,\"amount\":10000,"
                    + "\"transactionId\":\"OM12333\","
                    + "\"state\":\"FAILED\",\"errorCode\":\"AUTHORIZATION_ERROR\",\"detailedErrorCode\":\"ZM\","
                    + "\"splitInstruments\":[{\"rail\":{\"type\":\"PPI_EGV\"},"
                    + "\"instrument\":{\"type\":\"NET_BANKING\",\"bankTransactionId\":\"bankTransactionId\","
                    + "\"bankId\":\"bankId\",\"brn\":\"brn\",\"arn\":\"arn\"},\"amount\":10000}]}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("pg.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .orderId("OMOxx")
                                        .merchantId("merchantId")
                                        .merchantOrderId("merchantOrderId")
                                        .state("EXPIRED")
                                        .amount(10000L)
                                        .expireAt(1291391291L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("")
                                                        .udf2("")
                                                        .udf3("")
                                                        .udf4("")
                                                        .udf5("")
                                                        .build())
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_COLLECT)
                                                                .timestamp(12121212)
                                                                .amount(10000)
                                                                .transactionId("OM12333")
                                                                .state("FAILED")
                                                                .errorCode("AUTHORIZATION_ERROR")
                                                                .detailedErrorCode("ZM")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .rail(
                                                                                                PpiEgvPaymentRail
                                                                                                        .builder()
                                                                                                        .build())
                                                                                        .instrument(
                                                                                                NetBankingPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .arn(
                                                                                                                "arn")
                                                                                                        .brn(
                                                                                                                "brn")
                                                                                                        .bankId(
                                                                                                                "bankId")
                                                                                                        .bankTransactionId(
                                                                                                                "bankTransactionId")
                                                                                                        .build())
                                                                                        .amount(
                                                                                                10000)
                                                                                        .build()))
                                                                .build()))
                                        .build())
                        .build();
        CallbackResponse actual =
                customCheckoutClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void validateCallbackSubscription() {
        String jsonString =
                "{\"event\": \"subscription.cancelled\", \"payload\": {\"merchantSubscriptionId\":"
                        + " \"MS1708797962855\", \"subscriptionId\": \"OMS2402242336054995042603\","
                        + " \"state\": \"CANCELLED\", \"authWorkflowType\": \"TRANSACTION\","
                        + " \"amountType\": \"FIXED\", \"maxAmount\": 200, \"frequency\":"
                        + " \"ON_DEMAND\", \"expireAt\": 1737278524000, \"pauseStartDate\":"
                        + " 1708798426196, \"pauseEndDate\": 1708885799000}}";
        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("subscription.cancelled")
                        .payload(
                                CallbackData.builder()
                                        .merchantSubscriptionId("MS1708797962855")
                                        .subscriptionId("OMS2402242336054995042603")
                                        .state("CANCELLED")
                                        .authWorkflowType(AuthWorkflowType.TRANSACTION)
                                        .amountType(AmountType.FIXED)
                                        .maxAmount(200L)
                                        .frequency(Frequency.ON_DEMAND)
                                        .expireAt(1737278524000L)
                                        .pauseStartDate(1708798426196L)
                                        .pauseEndDate(1708885799000L)
                                        .build())
                        .build();

        CallbackResponse actual =
                subscriptionClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void subscriptionSetupSuccess() {
        String jsonString =
                "{\"event\": \"subscription.setup.order.completed\", \"payload\": {\"merchantId\":"
                    + " \"MID123\", \"merchantOrderId\": \"MO1708797962855\", \"orderId\":"
                    + " \"OMO2402242336055135042802\", \"state\": \"COMPLETED\", \"amount\": 200,"
                    + " \"expireAt\": 1708798385505, \"paymentFlow\": {\"type\":"
                    + " \"SUBSCRIPTION_SETUP\", \"merchantSubscriptionId\": \"MS1708797962855\","
                    + " \"authWorkflowType\": \"TRANSACTION\", \"amountType\": \"FIXED\","
                    + " \"maxAmount\": 200, \"frequency\": \"ON_DEMAND\", \"subscriptionId\":"
                    + " \"OMS2402242336054995042603\"}, \"errorCode\": \"<PRESENT ONLY IF STATE IS"
                    + " FAILED>\", \"detailedErrorCode\": \"<PRESENT ONLY IF STATE IS FAILED>\","
                    + " \"paymentDetails\": [{\"transactionId\": \"OM2402242336055865042862\","
                    + " \"paymentMode\": \"UPI_INTENT\", \"timestamp\": 1708797965588, \"amount\":"
                    + " 200, \"state\": \"COMPLETED\", \"splitInstruments\":[{\"instrument\":"
                    + " {\"type\": \"ACCOUNT\", \"maskedAccountNumber\": \"XXXXXXX20000\","
                    + " \"ifsc\": \"AABE0000000\", \"accountHolderName\": \"AABE0000000\","
                    + " \"accountType\": \"SAVINGS\"}, \"rail\": {\"type\": \"UPI\", \"utr\":"
                    + " \"405554491450\", \"vpa\": \"8668594479@ybl\", \"umn\":"
                    + " \"d519347eb2374125bcad6e69a42cc13b@ybl\"},\"amount\":200}], \"errorCode\":"
                    + " \"<PRESENT ONLY IF TRANSACTION IS FAILED>\", \"detailedErrorCode\":"
                    + " \"<PRESENT ONLY IF TRANSACTION IS FAILED>\"}]}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("subscription.setup.order.completed")
                        .payload(
                                CallbackData.builder()
                                        .merchantId("MID123")
                                        .merchantOrderId("MO1708797962855")
                                        .orderId("OMO2402242336055135042802")
                                        .state("COMPLETED")
                                        .amount(200L)
                                        .expireAt(1708798385505L)
                                        .paymentFlow(
                                                SubscriptionSetupPaymentFlowResponse.builder()
                                                        .merchantSubscriptionId("MS1708797962855")
                                                        .authWorkflowType(
                                                                AuthWorkflowType.TRANSACTION)
                                                        .amountType(AmountType.FIXED)
                                                        .maxAmount(200L)
                                                        .frequency(Frequency.ON_DEMAND)
                                                        .subscriptionId("OMS2402242336054995042603")
                                                        .build())
                                        .errorCode("<PRESENT ONLY IF STATE IS FAILED>")
                                        .detailedErrorCode("<PRESENT ONLY IF STATE IS FAILED>")
                                        .paymentDetails(
                                                Arrays.asList(
                                                        PaymentDetail.builder()
                                                                .transactionId(
                                                                        "OM2402242336055865042862")
                                                                .paymentMode(
                                                                        PgV2InstrumentType
                                                                                .UPI_INTENT)
                                                                .timestamp(1708797965588L)
                                                                .amount(200)
                                                                .state("COMPLETED")
                                                                .splitInstruments(
                                                                        Arrays.asList(
                                                                                InstrumentCombo
                                                                                        .builder()
                                                                                        .instrument(
                                                                                                AccountPaymentInstrumentV2
                                                                                                        .builder()
                                                                                                        .maskedAccountNumber(
                                                                                                                "XXXXXXX20000")
                                                                                                        .ifsc(
                                                                                                                "AABE0000000")
                                                                                                        .accountHolderName(
                                                                                                                "AABE0000000")
                                                                                                        .accountType(
                                                                                                                "SAVINGS")
                                                                                                        .build())
                                                                                        .rail(
                                                                                                UpiPaymentRail
                                                                                                        .builder()
                                                                                                        .utr(
                                                                                                                "405554491450")
                                                                                                        .vpa(
                                                                                                                "8668594479@ybl")
                                                                                                        .umn(
                                                                                                                "d519347eb2374125bcad6e69a42cc13b@ybl")
                                                                                                        .build())
                                                                                        .amount(200)
                                                                                        .build()))
                                                                .detailedErrorCode(
                                                                        "<PRESENT ONLY IF"
                                                                                + " TRANSACTION IS"
                                                                                + " FAILED>")
                                                                .errorCode(
                                                                        "<PRESENT ONLY IF"
                                                                                + " TRANSACTION IS"
                                                                                + " FAILED>")
                                                                .build()))
                                        .build())
                        .build();

        CallbackResponse actual =
                subscriptionClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testNotifyValidate() {
        String jsonString =
                "{\"event\": \"subscription.notification.completed\", \"payload\": {\"merchantId\":"
                    + " \"MID123\", \"merchantOrderId\": \"MO1708797962855\", \"orderId\":"
                    + " \"OMO12344\", \"amount\": 100, \"state\": \"NOTIFIED\", \"expireAt\":"
                    + " 1620891733101, \"metaInfo\": {\"udf1\": \"<some meta info of max length"
                    + " 256>\", \"udf2\": \"<some meta info of max length 256>\", \"udf3\": \"<some"
                    + " meta info of max length 256>\", \"udf4\": \"<some meta info of max length"
                    + " 256>\", \"udf5\": \"<some meta info of max length 256>\"}, \"paymentFlow\":"
                    + " {\"type\": \"SUBSCRIPTION_REDEMPTION\", \"merchantSubscriptionId\":"
                    + " \"MS121312\", \"redemptionRetryStrategy\": \"CUSTOM\", \"autoDebit\": true,"
                    + " \"validAfter\": \"1628229131000\", \"validUpto\": \"1628574731000\","
                    + " \"notifiedAt\": \"1622539751586\"}}}";

        CallbackResponse expected =
                CallbackResponse.builder()
                        .event("subscription.notification.completed")
                        .payload(
                                CallbackData.builder()
                                        .merchantId("MID123")
                                        .merchantOrderId("MO1708797962855")
                                        .orderId("OMO12344")
                                        .amount(100L)
                                        .state("NOTIFIED")
                                        .expireAt(1620891733101L)
                                        .metaInfo(
                                                MetaInfo.builder()
                                                        .udf1("<some meta info of max length 256>")
                                                        .udf2("<some meta info of max length 256>")
                                                        .udf3("<some meta info of max length 256>")
                                                        .udf4("<some meta info of max length 256>")
                                                        .udf5("<some meta info of max length 256>")
                                                        .build())
                                        .paymentFlow(
                                                SubscriptionRedemptionPaymentFlowResponse.builder()
                                                        .merchantSubscriptionId("MS121312")
                                                        .redemptionRetryStrategy(
                                                                RedemptionRetryStrategy.CUSTOM)
                                                        .autoDebit(true)
                                                        .validAfter(1628229131000L)
                                                        .validUpto(1628574731000L)
                                                        .notifiedAt(1622539751586L)
                                                        .build())
                                        .build())
                        .build();

        CallbackResponse actual =
                subscriptionClient.validateCallback(
                        "username",
                        "password",
                        "bc842c31a9e54efe320d30d948be61291f3ceee4766e36ab25fa65243cd76e0e",
                        jsonString);
        Assertions.assertEquals(expected, actual);
    }
}
