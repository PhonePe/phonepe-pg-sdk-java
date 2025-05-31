package com.phonepe.sdk.pg.common.models.response.rails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PpiEgvPaymentRail extends PaymentRail {

    @Builder
    public PpiEgvPaymentRail() {
        super(PaymentRailType.PPI_EGV);
    }
}
