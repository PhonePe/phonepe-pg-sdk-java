package com.phonepe.sdk.pg.common.models.request.instruments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class PpeIntenPaymentV2Instrument extends PaymentV2Instrument {

    @Builder
    public PpeIntenPaymentV2Instrument() {
        super(PgV2InstrumentType.PPE_INTENT);
    }
}
