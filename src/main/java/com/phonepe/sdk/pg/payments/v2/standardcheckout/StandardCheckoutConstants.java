/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
