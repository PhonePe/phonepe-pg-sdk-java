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
import com.phonepe.sdk.pg.common.models.request.DeviceContext;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.request.instruments.IntentPaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UpiIntentPayRequestBuilderTest {

    @Test
    void testBuildWithRequiredFieldsOnly() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_001")
                        .amount(100L)
                        .targetApp("com.phonepe.app")
                        .build();

        Assertions.assertNotNull(request);
        Assertions.assertEquals("ORDER_INTENT_001", request.getMerchantOrderId());
        Assertions.assertEquals(100L, request.getAmount());
        Assertions.assertNull(request.getMetaInfo());
        Assertions.assertNull(request.getExpireAfter());
    }

    @Test
    void testPaymentFlowIsSetToPg() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_002")
                        .amount(200L)
                        .targetApp("com.phonepe.app")
                        .build();

        Assertions.assertNotNull(request.getPaymentFlow());
        Assertions.assertTrue(request.getPaymentFlow() instanceof PgPaymentFlow);
        Assertions.assertEquals(PaymentFlowType.PG, request.getPaymentFlow().getType());
    }

    @Test
    void testPaymentModeIsUpiIntent() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_003")
                        .amount(300L)
                        .targetApp("com.google.android.apps.nbu.paisa.user")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertTrue(flow.getPaymentMode() instanceof IntentPaymentV2Instrument);
        IntentPaymentV2Instrument instrument = (IntentPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(PgV2InstrumentType.UPI_INTENT, instrument.getType());
    }

    @Test
    void testTargetAppIsCorrectlySet() {
        String expectedTargetApp = "net.one97.paytm";
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_004")
                        .amount(400L)
                        .targetApp(expectedTargetApp)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        IntentPaymentV2Instrument instrument = (IntentPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(expectedTargetApp, instrument.getTargetApp());
    }

    @Test
    void testDeviceContextIsSetWhenDeviceOsAndCallBackSchemeProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_005")
                        .amount(500L)
                        .targetApp("com.phonepe.app")
                        .deviceOS("ANDROID")
                        .merchantCallBackScheme("merchantapp://callback")
                        .build();

        Assertions.assertNotNull(request.getDeviceContext());
        Assertions.assertEquals("ANDROID", request.getDeviceContext().getDeviceOS());
        Assertions.assertEquals(
                "merchantapp://callback",
                request.getDeviceContext().getMerchantCallBackScheme());
    }

    @Test
    void testDeviceContextDeviceOsIsNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_006")
                        .amount(600L)
                        .targetApp("com.phonepe.app")
                        .build();

        DeviceContext deviceContext = request.getDeviceContext();
        Assertions.assertNotNull(deviceContext);
        Assertions.assertNull(deviceContext.getDeviceOS());
    }

    @Test
    void testExpireAfterIsSetWhenProvided() {
        long expireAfter = 600L;
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_007")
                        .amount(700L)
                        .targetApp("com.phonepe.app")
                        .expireAfter(expireAfter)
                        .build();

        Assertions.assertEquals(expireAfter, request.getExpireAfter());
    }

    @Test
    void testNoMerchantUrlsForUpiIntent() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_008")
                        .amount(800L)
                        .targetApp("com.phonepe.app")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertNull(flow.getMerchantUrls());
    }

    @Test
    void testConstraintsAreNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.UpiIntentPayRequestBuilder()
                        .merchantOrderId("ORDER_INTENT_009")
                        .amount(900L)
                        .targetApp("com.phonepe.app")
                        .build();

        Assertions.assertNull(request.getConstraints());
    }
}
