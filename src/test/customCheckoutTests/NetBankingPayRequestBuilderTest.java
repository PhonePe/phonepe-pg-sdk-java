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
import com.phonepe.sdk.pg.common.models.request.instruments.NetBankingPaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.models.request.PgPaymentFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NetBankingPayRequestBuilderTest {

    @Test
    void testBuildWithAllFields() {
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_001")
                        .amount(1000L)
                        .bankId("HDFC")
                        .merchantUserId("USER_001")
                        .redirectUrl("https://merchant.example.com/redirect")
                        .expireAfter(600L)
                        .build();

        Assertions.assertNotNull(request);
        Assertions.assertEquals("ORDER_NB_001", request.getMerchantOrderId());
        Assertions.assertEquals(1000L, request.getAmount());
    }

    @Test
    void testPaymentFlowIsSetToPg() {
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_002")
                        .amount(200L)
                        .bankId("ICICI")
                        .redirectUrl("https://merchant.example.com/redirect")
                        .build();

        Assertions.assertNotNull(request.getPaymentFlow());
        Assertions.assertTrue(request.getPaymentFlow() instanceof PgPaymentFlow);
        Assertions.assertEquals(PaymentFlowType.PG, request.getPaymentFlow().getType());
    }

    @Test
    void testPaymentModeIsNetBanking() {
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_003")
                        .amount(300L)
                        .bankId("SBI")
                        .redirectUrl("https://merchant.example.com/redirect")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertTrue(flow.getPaymentMode() instanceof NetBankingPaymentV2Instrument);
        NetBankingPaymentV2Instrument instrument =
                (NetBankingPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(PgV2InstrumentType.NET_BANKING, instrument.getType());
    }

    @Test
    void testBankIdIsCorrectlySet() {
        String expectedBankId = "AXIS";
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_004")
                        .amount(400L)
                        .bankId(expectedBankId)
                        .redirectUrl("https://merchant.example.com/redirect")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        NetBankingPaymentV2Instrument instrument =
                (NetBankingPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(expectedBankId, instrument.getBankId());
    }

    @Test
    void testMerchantUserIdIsCorrectlySet() {
        String expectedUserId = "USER_NB_123";
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_005")
                        .amount(500L)
                        .bankId("HDFC")
                        .merchantUserId(expectedUserId)
                        .redirectUrl("https://merchant.example.com/redirect")
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        NetBankingPaymentV2Instrument instrument =
                (NetBankingPaymentV2Instrument) flow.getPaymentMode();
        Assertions.assertEquals(expectedUserId, instrument.getMerchantUserId());
    }

    @Test
    void testMerchantUrlsRedirectUrlIsCorrectlySet() {
        String expectedRedirectUrl = "https://merchant.example.com/nb/callback";
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_006")
                        .amount(600L)
                        .bankId("KOTAK")
                        .redirectUrl(expectedRedirectUrl)
                        .build();

        PgPaymentFlow flow = (PgPaymentFlow) request.getPaymentFlow();
        Assertions.assertNotNull(flow.getMerchantUrls());
        Assertions.assertEquals(expectedRedirectUrl, flow.getMerchantUrls().getRedirectUrl());
    }

    @Test
    void testExpireAfterIsSetWhenProvided() {
        long expireAfter = 900L;
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_007")
                        .amount(700L)
                        .bankId("PNB")
                        .redirectUrl("https://merchant.example.com/redirect")
                        .expireAfter(expireAfter)
                        .build();

        Assertions.assertEquals(expireAfter, request.getExpireAfter());
    }

    @Test
    void testExpireAfterIsNullWhenNotProvided() {
        PgPaymentRequest request =
                PgPaymentRequest.NetBankingPayRequestBuilder()
                        .merchantOrderId("ORDER_NB_008")
                        .amount(800L)
                        .bankId("BOB")
                        .redirectUrl("https://merchant.example.com/redirect")
                        .build();

        Assertions.assertNull(request.getExpireAfter());
    }
}
