package com.phonepe.sdk.pg.payments.v2.standardcheckout;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StandardCheckoutConstants {

    public static final String PAY_API = "/checkout/v2/pay";
    public static final String CREATE_ORDER_API = "/checkout/v2/sdk/order";
    public static final String ORDER_STATUS_API = "/checkout/v2/order/%s/status";
    public static final String TRANSACTION_STATUS_API = "/checkout/v2/transaction/%s/status";
    public static final String REFUND_API = "/payments/v2/refund";
    public static final String REFUND_STATUS_API = "/payments/v2/refund/%s/status";
    public static final String ORDER_DETAILS = "details";
}
