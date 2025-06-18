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
package com.phonepe.sdk.pg.common.models.response.rails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgPaymentRail extends PaymentRail {

    private String transactionId;
    private String authorizationCode;
    private String serviceTransactionId;

    public PgPaymentRail() {
        super(PaymentRailType.PG);
    }

    @Builder
    public PgPaymentRail(
            String transactionId, String authorizationCode, String serviceTransactionId) {
        super(PaymentRailType.PG);
        this.transactionId = transactionId;
        this.authorizationCode = authorizationCode;
        this.serviceTransactionId = serviceTransactionId;
    }
}
