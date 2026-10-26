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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.AccountPaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.CreditLinePaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.PaymentInstrumentV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreditLinePaymentInstrumentV2Test {

    private static CreditLinePaymentInstrumentV2 sample() {
        return CreditLinePaymentInstrumentV2.builder()
                .maskedAccountNumber("121212121212")
                .ifsc("AABE0000000")
                .accountHolderName("accountHolderName")
                .bankId("bankId")
                .providerAccountType("providerAccountType")
                .build();
    }

    @Test
    void typeIsCreditLine() {
        Assertions.assertEquals(
                PaymentInstrumentV2.PaymentInstrumentType.CREDIT_LINE, sample().getType());
        Assertions.assertEquals(
                PaymentInstrumentV2.PaymentInstrumentType.CREDIT_LINE,
                new CreditLinePaymentInstrumentV2().getType());
    }

    @Test
    void settersAndGetters() {
        CreditLinePaymentInstrumentV2 instrument = new CreditLinePaymentInstrumentV2();
        instrument.setMaskedAccountNumber("121212121212");
        instrument.setIfsc("AABE0000000");
        instrument.setAccountHolderName("accountHolderName");
        instrument.setBankId("bankId");
        instrument.setProviderAccountType("providerAccountType");

        Assertions.assertEquals("121212121212", instrument.getMaskedAccountNumber());
        Assertions.assertEquals("AABE0000000", instrument.getIfsc());
        Assertions.assertEquals("accountHolderName", instrument.getAccountHolderName());
        Assertions.assertEquals("bankId", instrument.getBankId());
        Assertions.assertEquals("providerAccountType", instrument.getProviderAccountType());
        Assertions.assertEquals(sample(), instrument);
    }

    @Test
    void equalsAndHashCode() {
        CreditLinePaymentInstrumentV2 base = sample();
        CreditLinePaymentInstrumentV2 same = sample();

        Assertions.assertEquals(base, base);
        Assertions.assertEquals(base, same);
        Assertions.assertEquals(base.hashCode(), same.hashCode());
        Assertions.assertNotEquals(base, null);
        Assertions.assertNotEquals(base, "string");
        Assertions.assertNotEquals(base, AccountPaymentInstrumentV2.builder().build());

        CreditLinePaymentInstrumentV2 other = sample();
        other.setMaskedAccountNumber("other");
        Assertions.assertNotEquals(base, other);
        other = sample();
        other.setIfsc("other");
        Assertions.assertNotEquals(base, other);
        other = sample();
        other.setAccountHolderName("other");
        Assertions.assertNotEquals(base, other);
        other = sample();
        other.setBankId("other");
        Assertions.assertNotEquals(base, other);
        other = sample();
        other.setProviderAccountType("other");
        Assertions.assertNotEquals(base, other);
    }

    @Test
    void equalsAndHashCodeWithNullFields() {
        CreditLinePaymentInstrumentV2 empty = CreditLinePaymentInstrumentV2.builder().build();
        CreditLinePaymentInstrumentV2 anotherEmpty =
                CreditLinePaymentInstrumentV2.builder().build();

        Assertions.assertEquals(empty, anotherEmpty);
        Assertions.assertEquals(empty.hashCode(), anotherEmpty.hashCode());
        Assertions.assertNotEquals(empty, sample());
        Assertions.assertNotEquals(sample(), empty);

        CreditLinePaymentInstrumentV2 partial = CreditLinePaymentInstrumentV2.builder().build();
        partial.setMaskedAccountNumber("121212121212");
        Assertions.assertNotEquals(partial, sample());
        partial.setIfsc("AABE0000000");
        Assertions.assertNotEquals(partial, sample());
        partial.setAccountHolderName("accountHolderName");
        Assertions.assertNotEquals(partial, sample());
        partial.setBankId("bankId");
        Assertions.assertNotEquals(partial, sample());
        partial.setProviderAccountType("providerAccountType");
        Assertions.assertEquals(partial, sample());
    }

    @Test
    void toStringContainsFields() {
        String value = sample().toString();
        Assertions.assertTrue(value.contains("providerAccountType"));
        Assertions.assertTrue(value.contains("bankId"));
    }

    @Test
    void deserializesCreditLineAndIgnoresUnknownProperties() throws Exception {
        String json =
                "{\"type\":\"CREDIT_LINE\",\"ifsc\":\"AABE0000000\",\"unknown\":\"x\","
                        + "\"providerAccountType\":\"providerAccountType\"}";
        PaymentInstrumentV2 actual = new ObjectMapper().readValue(json, PaymentInstrumentV2.class);

        Assertions.assertTrue(actual instanceof CreditLinePaymentInstrumentV2);
        CreditLinePaymentInstrumentV2 creditLine = (CreditLinePaymentInstrumentV2) actual;
        Assertions.assertEquals("AABE0000000", creditLine.getIfsc());
        Assertions.assertEquals("providerAccountType", creditLine.getProviderAccountType());
    }
}
