package com.phonepe.sdk.pg.common.events.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionEventData {

    private String merchantSubscriptionId;
    private Long subscriptionExpireAt;
    private Long orderExpireAt;
}
