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
import com.phonepe.sdk.pg.common.models.request.instruments.VpaCollectPaymentDetails;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UpiCollectPayViaVpaRequestBuilderTest {

    @Test
    void testBuildWithRequiredFieldsOnly() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_001")
                        .amount(100L)
                        .vpa("test@upi")
                        .build();

        Assertions.assertNotNull(request);
        Assertions.assertEquals("ORDER_VPA_001", request.getMerchantOrderId());
        Assertions.assertEquals(100L, request.getAmount());
        Assertions.assertNull(request.getMetaInfo());
        Assertions.assertNull(request.getExpireAfter());
    }

    @Test
    void testPaymentFlowIsSetToPg() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_002")
                        .amount(200L)
                        .vpa("merchant@upi")
                        .build();

        Assertions.assertNotNull(request.getPaymentFlow());
        Assertions.assertTrue(request.getPaymentFlow() instanceof PgPaymentFlow);
        Assertions.assertEquals(PaymentFlowType.PG, request.getPaymentFlow().getType());
    }

    @Test
    void testPaymentModeIsUpiCollect() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_003")
                        .amount(300L)
                        .vpa("user@paytm")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertTrue(flow.getPaymentMode() instanceof CollectPaymentV2Instrument);
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(PgV2InstrumentType.UPI_COLLECT, instrument.getType());
    }

    @Test
    void testVpaIsCorrectlySet() {
        String expectedVpa = "john.doe@hdfc";
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_004")
                        .amount(400L)
                        .vpa(expectedVpa)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertTrue(instrument.getDetails() instanceof VpaCollectPaymentDetails);
        VpaCollectPaymentDetails details = (VpaCollectPaymentDetails) instrument.getDetails();
        Assertions.assertEquals(expectedVpa, details.getVpa());
    }

    @Test
    void testMessageIsSetWhenProvided() {
        String expectedMessage = "Payment for invoice #1234";
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_005")
                        .amount(500L)
                        .vpa("customer@sbi")
                        .message(expectedMessage)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(expectedMessage, instrument.getMessage());
    }

    @Test
    void testMessageIsNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_006")
                        .amount(600L)
                        .vpa("user@icici")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CollectPaymentV2Instrument instrument = (CollectPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertNull(instrument.getMessage());
    }

    @Test
    void testExpireAfterIsSetWhenProvided() {
        long expireAfter = 600L;
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_007")
                        .amount(700L)
                        .vpa("user@axis")
                        .expireAfter(expireAfter)
                        .build();

        Assertions.assertEquals(expireAfter, request.getExpireAfter());
    }

    @Test
    void testNoMerchantUrlsForUpiCollectVpa() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_008")
                        .amount(800L)
                        .vpa("user@ybl")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertNull(flow.getMerchantUrls());
    }

    @Test
    void testConstraintsAreNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
                        .merchantOrderId("ORDER_VPA_009")
                        .amount(900L)
                        .vpa("user@oksbi")
                        .build();

        Assertions.assertNull(request.getConstraints());
    }
}
