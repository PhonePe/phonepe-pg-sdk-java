package com.phonepe.sdk.pg.common.models.request.instruments;

import com.phonepe.sdk.pg.common.models.request.InstrumentConstraint;
import com.phonepe.sdk.pg.common.models.PgV2InstrumentType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class PaymentV2Instrument {

    private PgV2InstrumentType type;
    private List<InstrumentConstraint> instrumentConstraints;

    public PaymentV2Instrument(PgV2InstrumentType type) {
        this.type = type;
    }
}
