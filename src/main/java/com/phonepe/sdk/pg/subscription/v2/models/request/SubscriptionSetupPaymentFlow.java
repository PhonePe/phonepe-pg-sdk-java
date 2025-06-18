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
package com.phonepe.sdk.pg.subscription.v2.models.request;

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
public class SubscriptionSetupPaymentFlow extends PaymentFlow {

    private String merchantSubscriptionId;
    private AuthWorkflowType authWorkflowType;
    private AmountType amountType;
    private Long maxAmount;
    private Frequency frequency;
    private Long expireAt;
    private PaymentV2Instrument paymentMode;

    @Builder
    public SubscriptionSetupPaymentFlow(
            String merchantSubscriptionId,
            AuthWorkflowType authWorkflowType,
            AmountType amountType,
            Long maxAmount,
            Frequency frequency,
            Long expireAt,
            PaymentV2Instrument paymentMode) {
        super(PaymentFlowType.SUBSCRIPTION_SETUP);
        this.merchantSubscriptionId = merchantSubscriptionId;
        this.authWorkflowType = authWorkflowType;
        this.amountType = amountType;
        this.maxAmount = maxAmount;
        this.frequency = frequency;
        this.expireAt = expireAt;
        this.paymentMode = paymentMode;
    }
}
