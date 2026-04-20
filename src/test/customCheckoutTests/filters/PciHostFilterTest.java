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

import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import com.phonepe.sdk.pg.common.models.request.instruments.CardPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.CollectPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.IntentPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.NetBankingPaymentV2Instrument;
import com.phonepe.sdk.pg.common.models.request.instruments.TokenPaymentV2Instrument;
import com.phonepe.sdk.pg.payments.v2.customcheckout.filters.PayContext;
import com.phonepe.sdk.pg.payments.v2.customcheckout.filters.PciHostFilter;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PciHostFilterTest {

	private PciHostFilter filter;
	private final String defaultHost = "http://localhost:30419";
	private final String pciHost = Env.TEST.getPciPgHostUrl();

	@BeforeEach
	void setUp() {
		filter = new PciHostFilter(Env.TEST);
	}

	private PayContext ctx() {
		return new PayContext(defaultHost, new ArrayList<HttpHeaderPair>(), null);
	}

	// ── supports() ────────────────────────────────────────────────────────

	@Test
	void testDoesNotSupportNullInstrument() {
		Assertions.assertFalse(filter.supports(null));
	}

	@Test
	void testSupportsCardInstrument() {
		CardPaymentV2Instrument card = (CardPaymentV2Instrument) CardPaymentV2Instrument.builder()
				.build();
		Assertions.assertTrue(filter.supports(card));
	}

	@Test
	void testSupportsTokenInstrument() {
		TokenPaymentV2Instrument token = (TokenPaymentV2Instrument) TokenPaymentV2Instrument
				.builder()
				.build();
		Assertions.assertTrue(filter.supports(token));
	}

	@Test
	void testDoesNotSupportUpiCollect() {
		CollectPaymentV2Instrument upiCollect = CollectPaymentV2Instrument.builder().build();
		Assertions.assertFalse(filter.supports(upiCollect));
	}

	@Test
	void testDoesNotSupportUpiIntent() {
		IntentPaymentV2Instrument upiIntent = IntentPaymentV2Instrument.builder().build();
		Assertions.assertFalse(filter.supports(upiIntent));
	}

	@Test
	void testDoesNotSupportNetBanking() {
		NetBankingPaymentV2Instrument netBanking = NetBankingPaymentV2Instrument.builder().build();
		Assertions.assertFalse(filter.supports(netBanking));
	}

	// ── apply() ───────────────────────────────────────────────────────────

	@Test
	void testApplySetsHostUrlToPciHost() {
		PayContext ctx = ctx();
		Assertions.assertEquals(defaultHost, ctx.getHostUrl());
		filter.apply(ctx);
		Assertions.assertEquals(pciHost, ctx.getHostUrl());
	}

	@Test
	void testApplyDoesNotModifyHeaders() {
		PayContext ctx = ctx();
		filter.apply(ctx);
		Assertions.assertTrue(ctx.getHeaders().isEmpty());
	}
}
