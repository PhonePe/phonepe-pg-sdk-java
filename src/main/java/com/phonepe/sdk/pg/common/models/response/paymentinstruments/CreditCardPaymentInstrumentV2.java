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
package com.phonepe.sdk.pg.common.models.response.paymentinstruments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCardPaymentInstrumentV2 extends PaymentInstrumentV2 {

    private String bankTransactionId;
    private String bankId;
    private String brn;
    private String arn;

    public CreditCardPaymentInstrumentV2() {
        super(PaymentInstrumentType.CREDIT_CARD);
    }

    @Builder
    public CreditCardPaymentInstrumentV2(
            String bankTransactionId, String bankId, String brn, String arn) {
        super(PaymentInstrumentType.CREDIT_CARD);
        this.bankTransactionId = bankTransactionId;
        this.bankId = bankId;
        this.brn = brn;
        this.arn = arn;
    }
}
