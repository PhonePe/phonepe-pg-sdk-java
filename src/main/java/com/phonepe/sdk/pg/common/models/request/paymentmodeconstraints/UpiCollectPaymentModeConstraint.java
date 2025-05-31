package com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class UpiCollectPaymentModeConstraint extends PaymentModeConstraint {

    @JsonCreator
    public UpiCollectPaymentModeConstraint() {
        super(PgV2InstrumentType.UPI_COLLECT);
    }
}
