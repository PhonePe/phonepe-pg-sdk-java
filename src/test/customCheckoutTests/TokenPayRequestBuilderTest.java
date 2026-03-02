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
import com.phonepe.sdk.pg.common.models.request.instruments.TokenPaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenPayRequestBuilderTest {

    private PgPaymentRequest buildFullTokenRequest(String orderId) {
        return PgPaymentRequest.TokenPayRequestBuilder()
                .merchantOrderId(orderId)
                .amount(1000L)
                .encryptionKeyId(42L)
                .authMode("H2H")
                .encryptedToken("encTok123")
                .encryptedCvv("encCvv456")
                .cryptogram("crypto789")
                .panSuffix("1234")
                .expiryMonth("12")
                .expiryYear("2028")
                .redirectUrl("https://merchant.example.com/redirect")
                .cardHolderName("John Doe")
                .merchantUserId("USER_TOKEN_001")
                .expireAfter(600L)
                .build();
    }

    @Test
    void testBuildWithAllFields() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_001");

        Assertions.assertNotNull(request);
        Assertions.assertEquals("ORDER_TOKEN_001", request.getMerchantOrderId());
        Assertions.assertEquals(1000L, request.getAmount());
    }

    @Test
    void testPaymentFlowIsSetToPg() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_002");

        Assertions.assertNotNull(request.getPaymentFlow());
        Assertions.assertTrue(request.getPaymentFlow() instanceof PgPaymentFlow);
        Assertions.assertEquals(PaymentFlowType.PG, request.getPaymentFlow().getType());
    }

    @Test
    void testPaymentModeIsToken() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_003");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertTrue(flow.getPaymentMode() instanceof TokenPaymentV2Instrument);
        TokenPaymentV2Instrument instrument = (TokenPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(PgV2InstrumentType.TOKEN, instrument.getType());
    }

    @Test
    void testTokenDetailsFieldsAreCorrectlySet() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_004");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        TokenPaymentV2Instrument instrument = (TokenPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertNotNull(instrument.getTokenDetails());
        Assertions.assertEquals("encTok123", instrument.getTokenDetails().getEncryptedToken());
        Assertions.assertEquals("encCvv456", instrument.getTokenDetails().getEncryptedCvv());
        Assertions.assertEquals(42L, instrument.getTokenDetails().getEncryptionKeyId());
        Assertions.assertEquals("crypto789", instrument.getTokenDetails().getCryptogram());
        Assertions.assertEquals("1234", instrument.getTokenDetails().getPanSuffix());
        Assertions.assertEquals("John Doe", instrument.getTokenDetails().getCardHolderName());
    }

    @Test
    void testExpiryMonthAndYearAreCorrectlySet() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_005");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        TokenPaymentV2Instrument instrument = (TokenPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertNotNull(instrument.getTokenDetails().getExpiry());
        Assertions.assertEquals("12", instrument.getTokenDetails().getExpiry().getMonth());
        Assertions.assertEquals("2028", instrument.getTokenDetails().getExpiry().getYear());
    }

    @Test
    void testMerchantUrlsRedirectUrlIsCorrectlySet() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_006");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertNotNull(flow.getMerchantUrls());
        Assertions.assertEquals(
                "https://merchant.example.com/redirect",
                flow.getMerchantUrls().getRedirectUrl());
    }

    @Test
    void testMerchantUserIdIsCorrectlySet() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_007");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        TokenPaymentV2Instrument instrument = (TokenPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals("USER_TOKEN_001", instrument.getMerchantUserId());
    }

    @Test
    void testAuthModeIsCorrectlySet() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_008");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        TokenPaymentV2Instrument instrument = (TokenPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals("H2H", instrument.getAuthMode());
    }

    @Test
    void testExpireAfterIsSetWhenProvided() {
        PgPaymentRequest request = buildFullTokenRequest("ORDER_TOKEN_009");

        Assertions.assertEquals(600L, request.getExpireAfter());
    }
}
