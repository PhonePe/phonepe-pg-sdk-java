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
package com.phonepe.sdk.pg.subscription.v2.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.common.models.response.PaymentFlowResponse;
import com.phonepe.sdk.pg.subscription.v2.models.request.RedemptionRetryStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionRedemptionPaymentFlowResponse extends PaymentFlowResponse {

    private String merchantSubscriptionId;
    private RedemptionRetryStrategy redemptionRetryStrategy;
    private boolean autoDebit;
    private Long validAfter;
    private Long validUpto;
    private Long notifiedAt;

    public SubscriptionRedemptionPaymentFlowResponse() {
        super(PaymentFlowType.SUBSCRIPTION_REDEMPTION);
    }

    @Builder
    public SubscriptionRedemptionPaymentFlowResponse(
            String merchantSubscriptionId,
            RedemptionRetryStrategy redemptionRetryStrategy,
            boolean autoDebit,
            Long validAfter,
            Long validUpto,
            Long notifiedAt) {
        super(PaymentFlowType.SUBSCRIPTION_REDEMPTION);
        this.merchantSubscriptionId = merchantSubscriptionId;
        this.redemptionRetryStrategy = redemptionRetryStrategy;
        this.autoDebit = autoDebit;
        this.validAfter = validAfter;
        this.validUpto = validUpto;
        this.notifiedAt = notifiedAt;
    }
}
