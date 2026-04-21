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

import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.request.instruments.CardPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.PaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.TokenPaymentV2Instrument;
import lombok.RequiredArgsConstructor;

/**
 * Routes PCI-scoped instruments (Card, Token) to the dedicated PCI host URL.
 * Add new PCI instrument types to {@link #supports} as they are introduced.
 */
@RequiredArgsConstructor
public class PciHostFilter implements InstrumentRequestFilter {

	private final Env env;

	@Override
	public boolean supports(PaymentV2Instrument instrument) {
		if (instrument == null) {
			return false;
		}
		return instrument instanceof CardPaymentV2Instrument
				|| instrument instanceof TokenPaymentV2Instrument;
	}

	@Override
	public void apply(PayContext ctx) {
		ctx.setHostUrl(env.getPciPgHostUrl());
	}
}
