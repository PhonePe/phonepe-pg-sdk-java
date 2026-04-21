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

import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import com.phonepe.sdk.pg.common.models.request.instruments.PaymentV2Instrument;

/**
 * Injects the x-device-os header when deviceOS is present on the request.
 * Applies to all instrument types.
 */
public class DeviceOsHeaderFilter implements InstrumentRequestFilter {

	@Override
	public boolean supports(PaymentV2Instrument instrument) {
		return true;
	}

	@Override
	public void apply(PayContext ctx) {
		if (ctx.getDeviceOS() != null && !ctx.getDeviceOS()
				.isBlank()) {
			ctx.getHeaders()
					.add(
							HttpHeaderPair.builder()
									.key(Headers.X_DEVICE_OS)
									.value(ctx.getDeviceOS())
									.build());
		}
	}
}
