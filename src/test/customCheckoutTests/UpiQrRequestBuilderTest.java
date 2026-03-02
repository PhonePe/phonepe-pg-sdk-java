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
import com.phonepe.sdk.pg.common.models.request.instruments.UpiQrPaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UpiQrRequestBuilderTest {

    @Test
    void testBuildWithRequiredFieldsOnly() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("ORDER_QR_001")
                        .amount(100L)
                        .build();

        Assertions.assertNotNull(request);
        Assertions.assertEquals("ORDER_QR_001", request.getMerchantOrderId());
        Assertions.assertEquals(100L, request.getAmount());
        Assertions.assertNull(request.getMetaInfo());
        Assertions.assertNull(request.getExpireAfter());
    }

    @Test
    void testPaymentFlowIsSetToPg() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("ORDER_QR_002")
                        .amount(200L)
                        .build();

        Assertions.assertNotNull(request.getPaymentFlow());
        Assertions.assertTrue(request.getPaymentFlow() instanceof PgPaymentFlow);
        Assertions.assertEquals(PaymentFlowType.PG, request.getPaymentFlow().getType());
    }

    @Test
    void testPaymentModeIsUpiQr() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("ORDER_QR_003")
                        .amount(300L)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertTrue(flow.getPaymentMode() instanceof UpiQrPaymentV2Instrument);
        UpiQrPaymentV2Instrument instrument = (UpiQrPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(PgV2InstrumentType.UPI_QR, instrument.getType());
    }

    @Test
    void testNoMerchantUrlsForUpiQr() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("ORDER_QR_004")
                        .amount(400L)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertNull(flow.getMerchantUrls());
    }

    @Test
    void testNoDeviceContextForUpiQr() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("ORDER_QR_005")
                        .amount(500L)
                        .build();

        Assertions.assertNull(request.getDeviceContext());
    }

    @Test
    void testExpireAfterIsSetWhenProvided() {
        long expireAfter = 300L;
        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("ORDER_QR_006")
                        .amount(600L)
                        .expireAfter(expireAfter)
                        .build();

        Assertions.assertEquals(expireAfter, request.getExpireAfter());
    }

    @Test
    void testConstraintsAreNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiQrRequestBuilder()
                        .merchantOrderId("ORDER_QR_007")
                        .amount(700L)
                        .build();

        Assertions.assertNull(request.getConstraints());
    }
}
