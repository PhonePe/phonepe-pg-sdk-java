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
package customCheckoutTests.filters;

import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import com.phonepe.sdk.pg.common.models.request.instruments.CardPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.CollectPaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.customcheckout.filters.DeviceOsHeaderFilter;
import com.phonepe.sdk.pg.payments.v2.customcheckout.filters.PayContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeviceOsHeaderFilterTest {

	private DeviceOsHeaderFilter filter;
	private final String host = "http://localhost:30419";

	@BeforeEach
	void setUp() {
		filter = new DeviceOsHeaderFilter();
	}

	private PayContext ctx(String deviceOS) {
		return new PayContext(host, new ArrayList<HttpHeaderPair>(), deviceOS);
	}

	// ── supports() ────────────────────────────────────────────────────────

	@Test
	void testSupportsNullInstrument() {
		Assertions.assertTrue(filter.supports(null));
	}

	@Test
	void testSupportsCardInstrument() {
		Assertions.assertTrue(
				filter.supports((CardPaymentV2Instrument) CardPaymentV2Instrument.builder().build()));
	}

	@Test
	void testSupportsUpiCollectInstrument() {
		Assertions.assertTrue(filter.supports(CollectPaymentV2Instrument.builder().build()));
	}

	// ── apply() – header injection ─────────────────────────────────────────

	@Test
	void testApplyAddsDeviceOsHeaderWhenPresent() {
		PayContext ctx = ctx("ANDROID");
		filter.apply(ctx);

		List<HttpHeaderPair> headers = ctx.getHeaders();
		Assertions.assertEquals(1, headers.size());
		Assertions.assertEquals(Headers.X_DEVICE_OS, headers.get(0).getKey());
		Assertions.assertEquals("ANDROID", headers.get(0).getValue());
	}

	@Test
	void testApplyAddsIosDeviceOsHeader() {
		PayContext ctx = ctx("IOS");
		filter.apply(ctx);
		Assertions.assertEquals(1, ctx.getHeaders().size());
		Assertions.assertEquals("IOS", ctx.getHeaders().get(0).getValue());
	}

	@Test
	void testApplySkipsHeaderWhenDeviceOsIsNull() {
		PayContext ctx = ctx(null);
		filter.apply(ctx);
		Assertions.assertTrue(ctx.getHeaders().isEmpty());
	}

	@Test
	void testApplySkipsHeaderWhenDeviceOsIsBlank() {
		PayContext ctx = ctx("   ");
		filter.apply(ctx);
		Assertions.assertTrue(ctx.getHeaders().isEmpty());
	}

	@Test
	void testApplyDoesNotModifyHostUrl() {
		PayContext ctx = ctx("ANDROID");
		filter.apply(ctx);
		Assertions.assertEquals(host, ctx.getHostUrl());
	}
}
