package com.phonepe.sdk.pg.payments.v2.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phonepe.sdk.pg.common.models.request.paymentmodeconstraints.PaymentModeConstraint;
import java.util.List;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class PaymentModeConfig {

    List<PaymentModeConstraint> enabledPaymentModes;
    List<PaymentModeConstraint> disabledPaymentModes;

    PaymentModeConfig(List<PaymentModeConstraint> enabledPaymentModes,
            List<PaymentModeConstraint> disabledPaymentModes) {
        this.enabledPaymentModes = enabledPaymentModes;
        this.disabledPaymentModes = disabledPaymentModes;
    }

    public PaymentModeConfig() {
        this.enabledPaymentModes = null;
        this.disabledPaymentModes = null;
    }
}
