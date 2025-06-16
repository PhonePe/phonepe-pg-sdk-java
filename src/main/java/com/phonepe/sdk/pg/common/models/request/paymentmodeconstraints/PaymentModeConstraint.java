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
package com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardPaymentModeConstraint.class, name = "CARD"),
    @JsonSubTypes.Type(value = NetBankingPaymentModeConstraint.class, name = "NET_BANKING"),
    @JsonSubTypes.Type(value = UpiIntentPaymentModeConstraint.class, name = "UPI_INTENT"),
    @JsonSubTypes.Type(value = UpiQrPaymentModeConstraint.class, name = "UPI_QR"),
    @JsonSubTypes.Type(value = UpiCollectPaymentModeConstraint.class, name = "UPI_COLLECT")
})
public abstract class PaymentModeConstraint {

    protected PgV2InstrumentType type;
}
