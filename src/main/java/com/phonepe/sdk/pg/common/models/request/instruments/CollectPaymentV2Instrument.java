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
public class CollectPaymentV2Instrument extends PaymentV2Instrument {

    private CollectPaymentDetails details;
    private String message;

    @Builder
    public CollectPaymentV2Instrument(CollectPaymentDetails details, String message) {
        super(PgV2InstrumentType.UPI_COLLECT);
        this.details = details;
        this.message = message;
    }
}
