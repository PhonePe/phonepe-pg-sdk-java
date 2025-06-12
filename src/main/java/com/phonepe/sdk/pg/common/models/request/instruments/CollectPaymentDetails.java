package com.phonepe.sdk.pg.common.models.request.instruments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class CollectPaymentDetails {

    CollectPaymentDetailsType type;

}
