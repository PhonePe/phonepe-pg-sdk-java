package com.phonepe.sdk.pg.common.models.response.rails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PpiWalletPaymentRail extends PaymentRail {

    @Builder
    public PpiWalletPaymentRail() {
        super(PaymentRailType.PPI_WALLET);
    }
}
