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
package com.phonepe.sdk.pg.common.events.models;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.events.models.enums.EventState;
import com.phonepe.sdk.pg.common.events.models.enums.FlowType;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import com.phonepe.sdk.pg.common.models.request.DeviceContext;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventData {

    // Product Type Detail (PG, PG_CHECKOUT)
    private FlowType flowType;
    @Default private String sdkType = Headers.SDK_TYPE;
    @Default private String sdkVersion = Headers.SDK_VERSION;

    // API Details
    private String apiPath;
    private Long amount;
    private String targetApp;
    private DeviceContext deviceContext;
    private Long expireAfter;
    private String merchantRefundId;
    private String originalMerchantOrderId;
    private String transactionId;
    private EventState eventState;
    private PgV2InstrumentType paymentInstrument;

    // Token Details
    private Long cachedTokenIssuedAt;
    private Long cachedTokenExpiresAt;
    private Long tokenFetchAttemptTimestamp;

    // Subscription Details
    @JsonUnwrapped private SubscriptionEventData subscriptionEventData;

    // Exception Data
    private String exceptionClass;
    private String exceptionMessage;
    private String exceptionCode;
    private Integer exceptionHttpStatusCode;
    private Map<String, Object> exceptionData;
}
