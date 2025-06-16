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
package com.phonepe.sdk.pg.common.models.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.phonepe.sdk.pg.common.models.PaymentFlowType;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionRedemptionPaymentFlowResponse;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionSetupPaymentFlowResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(
            name = "SUBSCRIPTION_SETUP",
            value = SubscriptionSetupPaymentFlowResponse.class),
    @JsonSubTypes.Type(
            name = "SUBSCRIPTION_REDEMPTION",
            value = SubscriptionRedemptionPaymentFlowResponse.class),
})
@NoArgsConstructor
@Data
public class PaymentFlowResponse {

    private PaymentFlowType type;

    public PaymentFlowResponse(PaymentFlowType type) {
        this.type = type;
    }
}
