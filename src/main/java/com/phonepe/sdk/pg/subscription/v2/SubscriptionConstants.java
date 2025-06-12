package com.phonepe.sdk.pg.subscription.v2;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SubscriptionConstants {

    public static final String SETUP_API = "/subscriptions/v2/setup";
    public static final String NOTIFY_API = "/subscriptions/v2/notify";
    public static final String REDEEM_API = "/subscriptions/v2/redeem";
    public static final String SUBSCRIPTION_STATUS_API = "/subscriptions/v2/%s/status";
    public static final String ORDER_STATUS_API = "/subscriptions/v2/order/%s/status";
    public static final String CANCEL_API = "/subscriptions/v2/%s/cancel";
    public static final String TRANSACTION_STATUS_API = "/subscriptions/v2/transaction/%s/status";
    public static final String REFUND_API = "/payments/v2/refund";
    public static final String REFUND_STATUS_API = "/payments/v2/refund/%s/status";
}
