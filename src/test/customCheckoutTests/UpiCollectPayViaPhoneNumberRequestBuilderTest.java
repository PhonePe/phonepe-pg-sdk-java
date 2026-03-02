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

import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.request.instruments.CollectPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.PhoneNumberCollectPaymentDetails;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UpiCollectPayViaPhoneNumberRequestBuilderTest {

    @Test
    void testBuildWithRequiredFieldsOnly() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_001")
                        .amount(100L)
                        .phoneNumber("9876543210")
                        .build();

        Assertions.assertNotNull(request);
        Assertions.assertEquals("ORDER_PHONE_001", request.getMerchantOrderId());
        Assertions.assertEquals(100L, request.getAmount());
        Assertions.assertNull(request.getMetaInfo());
        Assertions.assertNull(request.getExpireAfter());
    }

    @Test
    void testPaymentFlowIsSetToPg() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_002")
                        .amount(200L)
                        .phoneNumber("9000000001")
                        .build();

        Assertions.assertNotNull(request.getPaymentFlow());
        Assertions.assertTrue(request.getPaymentFlow() instanceof PgPaymentFlow);
        Assertions.assertEquals(PaymentFlowType.PG, request.getPaymentFlow().getType());
    }

    @Test
    void testPaymentModeIsUpiCollect() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_003")
                        .amount(300L)
                        .phoneNumber("9000000002")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertTrue(flow.getPaymentMode() instanceof CollectPaymentV2Instrument);
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(PgV2InstrumentType.UPI_COLLECT, instrument.getType());
    }

    @Test
    void testPhoneNumberIsCorrectlySet() {
        String expectedPhone = "9123456789";
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_004")
                        .amount(400L)
                        .phoneNumber(expectedPhone)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertTrue(
                instrument.getDetails() instanceof PhoneNumberCollectPaymentDetails);
        PhoneNumberCollectPaymentDetails details =
                (PhoneNumberCollectPaymentDetails) instrument.getDetails();
        Assertions.assertEquals(expectedPhone, details.getPhoneNumber());
    }

    @Test
    void testMessageIsSetWhenProvided() {
        String expectedMessage = "Collect for order #5678";
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_005")
                        .amount(500L)
                        .phoneNumber("9111111111")
                        .message(expectedMessage)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(expectedMessage, instrument.getMessage());
    }

    @Test
    void testMessageIsNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_006")
                        .amount(600L)
                        .phoneNumber("9222222222")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertNull(instrument.getMessage());
    }

    @Test
    void testExpireAfterIsSetWhenProvided() {
        long expireAfter = 900L;
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_007")
                        .amount(700L)
                        .phoneNumber("9333333333")
                        .expireAfter(expireAfter)
                        .build();

        Assertions.assertEquals(expireAfter, request.getExpireAfter());
    }

    @Test
    void testNoMerchantUrlsForUpiCollectPhoneNumber() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_008")
                        .amount(800L)
                        .phoneNumber("9444444444")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertNull(flow.getMerchantUrls());
    }

    @Test
    void testConstraintsAreNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_009")
                        .amount(900L)
                        .phoneNumber("9555555555")
                        .build();

        Assertions.assertNull(request.getConstraints());
    }

    @Test
    void testDetailsTypeIsPhoneNumber() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_010")
                        .amount(1000L)
                        .phoneNumber("9666666666")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        PhoneNumberCollectPaymentDetails details =
                (PhoneNumberCollectPaymentDetails) instrument.getDetails();
        Assertions.assertEquals(
                com.phonepe.sdk.pg.common.models.request.instruments.CollectPaymentDetailsType
                        .PHONE_NUMBER,
                details.getType());
    }

    @Test
    void testXDeviceOsIsSetWhenProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_011")
                        .amount(1100L)
                        .phoneNumber("9777777777")
                        .deviceOS("ANDROID")
                        .build();

        Assertions.assertEquals("ANDROID", request.getDeviceOS());
    }

    @Test
    void testXDeviceOsIsNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_012")
                        .amount(1200L)
                        .phoneNumber("9888888888")
                        .build();

        Assertions.assertNull(request.getDeviceOS());
    }

    @Test
    void testXDeviceOsNotInJsonSerialization() throws Exception {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaPhoneNumberRequestBuilder()
                        .merchantOrderId("ORDER_PHONE_013")
                        .amount(1300L)
                        .phoneNumber("9999999999")
                        .deviceOS("IOS")
                        .build();

        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request);
        Assertions.assertFalse(json.contains("xDeviceOs"));
    }
}
