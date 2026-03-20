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
package com.phonepe.sdk.pg.payments.v2.customcheckout.filters;

import com.phonepe.sdk.pg.common.models.request.instruments.PaymentV2Instrument;

/**
 * Strategy interface for modifying pay request context (host URL, headers)
 * based on the payment instrument. Implement this to add new routing or
 * header injection rules without modifying CustomCheckoutClient.
 */
public interface InstrumentRequestFilter {

	/**
	 * Returns true if this filter applies to the given instrument.
	 */
	boolean supports(PaymentV2Instrument instrument);

	/**
	 * Applies modifications to the PayContext (host URL and/or headers).
	 */
	void apply(PayContext ctx);
}
