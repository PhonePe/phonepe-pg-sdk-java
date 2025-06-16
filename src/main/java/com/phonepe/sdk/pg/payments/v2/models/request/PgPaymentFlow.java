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
package com.phonepe.sdk.pg.payments.v2.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.common.models.request.PaymentFlow;
import com.phonepe.sdk.pg.common.models.request.instruments.PaymentV2Instrument;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class PgPaymentFlow extends PaymentFlow {

    private PaymentV2Instrument paymentMode;
    private MerchantUrls merchantUrls;

    @Builder
    public PgPaymentFlow(final PaymentV2Instrument paymentMode, final MerchantUrls merchantUrls) {
        super(PaymentFlowType.PG);
        this.paymentMode = paymentMode;
        this.merchantUrls = merchantUrls;
    }
}
