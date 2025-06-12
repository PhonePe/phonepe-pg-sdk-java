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
public class IntentPaymentV2Instrument extends PaymentV2Instrument {

    private String targetApp;

    public IntentPaymentV2Instrument() {
        super(PgV2InstrumentType.UPI_INTENT);
    }

    @Builder
    public IntentPaymentV2Instrument(String targetApp) {
        super(PgV2InstrumentType.UPI_INTENT);
        this.targetApp = targetApp;
    }
}
