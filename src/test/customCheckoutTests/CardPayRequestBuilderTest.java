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
import com.phonepe.sdk.pg.common.models.request.instruments.CardPaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CardPayRequestBuilderTest {

    private PgPaymentRequest buildFullCardRequest(String orderId) {
        return PgPaymentRequest.CardPayRequestBuilder()
                .merchantOrderId(orderId)
                .amount(2000L)
                .encryptionKeyId(99L)
                .authMode("H2H")
                .encryptedCardNumber("encCard9876")
                .encryptedCvv("encCvv111")
                .expiryMonth("06")
                .expiryYear("2027")
                .cardHolderName("Jane Smith")
                .merchantUserId("USER_CARD_001")
                .redirectUrl("https://merchant.example.com/card/redirect")
                .expireAfter(900L)
                .build();
    }

    @Test
    void testBuildWithAllFields() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_001");

        Assertions.assertNotNull(request);
        Assertions.assertEquals("ORDER_CARD_001", request.getMerchantOrderId());
        Assertions.assertEquals(2000L, request.getAmount());
    }

    @Test
    void testPaymentFlowIsSetToPg() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_002");

        Assertions.assertNotNull(request.getPaymentFlow());
        Assertions.assertTrue(request.getPaymentFlow() instanceof PgPaymentFlow);
        Assertions.assertEquals(PaymentFlowType.PG, request.getPaymentFlow().getType());
    }

    @Test
    void testPaymentModeIsCard() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_003");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertTrue(flow.getPaymentMode() instanceof CardPaymentV2Instrument);
        CardPaymentV2Instrument instrument = (CardPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(PgV2InstrumentType.CARD, instrument.getType());
    }

    @Test
    void testCardDetailsFieldsAreCorrectlySet() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_004");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CardPaymentV2Instrument instrument = (CardPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertNotNull(instrument.getCardDetails());
        Assertions.assertEquals(
                "encCard9876", instrument.getCardDetails().getEncryptedCardNumber());
        Assertions.assertEquals("encCvv111", instrument.getCardDetails().getEncryptedCvv());
        Assertions.assertEquals(99L, instrument.getCardDetails().getEncryptionKeyId());
        Assertions.assertEquals("Jane Smith", instrument.getCardDetails().getCardHolderName());
    }

    @Test
    void testCardExpiryIsCorrectlySet() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_005");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CardPaymentV2Instrument instrument = (CardPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertNotNull(instrument.getCardDetails().getExpiry());
        Assertions.assertEquals("06", instrument.getCardDetails().getExpiry().getMonth());
        Assertions.assertEquals("2027", instrument.getCardDetails().getExpiry().getYear());
    }

    @Test
    void testMerchantUrlsRedirectUrlIsCorrectlySet() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_006");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertNotNull(flow.getMerchantUrls());
        Assertions.assertEquals(
                "https://merchant.example.com/card/redirect",
                flow.getMerchantUrls().getRedirectUrl());
    }

    @Test
    void testMerchantUserIdIsCorrectlySet() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_007");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CardPaymentV2Instrument instrument = (CardPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals("USER_CARD_001", instrument.getMerchantUserId());
    }

    @Test
    void testAuthModeIsCorrectlySet() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_008");

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        CardPaymentV2Instrument instrument = (CardPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals("H2H", instrument.getAuthMode());
    }

    @Test
    void testExpireAfterIsSetWhenProvided() {
        PgPaymentRequest request = buildFullCardRequest("ORDER_CARD_009");

        Assertions.assertEquals(900L, request.getExpireAfter());
    }
}
