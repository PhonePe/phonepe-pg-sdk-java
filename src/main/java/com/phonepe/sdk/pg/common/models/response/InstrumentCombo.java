package com.phonepe.sdk.pg.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phonepe.sdk.pg.common.models.response.paymentinstruments.PaymentInstrumentV2;
import com.phonepe.sdk.pg.common.models.response.rails.PaymentRail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstrumentCombo {

    private PaymentInstrumentV2 instrument;
    private PaymentRail rail;
    private long amount;

}
