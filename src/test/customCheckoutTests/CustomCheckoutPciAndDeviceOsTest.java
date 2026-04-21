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
package customCheckoutTests;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.phonepe.sdk.pg.common.constants.Headers;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.payments.v2.customcheckout.CustomCheckoutConstants;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpStatus;

/**
 * Tests for PCI host routing and DeviceOS header injection in CustomCheckoutClient.pay().
 */
class CustomCheckoutPciAndDeviceOsTest extends CustomCheckoutBaseSetup {

	// ── PCI host routing — Card ───────────────────────────────────────────

	@Test
	void testCardPayRoutesToPciHost() {
		final String url = CustomCheckoutConstants.PAY_API;

		PgPaymentRequest request = PgPaymentRequest.CardPayRequestBuilder()
				.merchantOrderId("ORDER_PCI_CARD_001")
				.amount(5000L)
				.encryptionKeyId(1L)
				.authMode("H2H")
				.encryptedCardNumber("encCard123")
				.encryptedCvv("encCvv456")
				.expiryMonth("12")
				.expiryYear("2028")
				.cardHolderName("Test User")
				.redirectUrl("https://merchant.com/redirect")
				.build();

		PgPaymentResponse response = PgPaymentResponse.builder()
				.orderId("OMO_PCI_CARD_001")
				.state("PENDING")
				.expireAt(java.time.Instant.now().getEpochSecond() + 600)
				.build();

		// WireMock is on localhost:30419 = Env.TEST pciPgHostUrl — request must arrive here
		addStubForPostRequest(url, getHeaders(), request, HttpStatus.SC_OK, Maps.newHashMap(),
				response);

		PgPaymentResponse actual = customCheckoutClient.pay(request);
		Assertions.assertEquals(response.getOrderId(), actual.getOrderId());
	}

	// ── PCI host routing — Token ──────────────────────────────────────────

	@Test
	void testTokenPayRoutesToPciHost() {
		final String url = CustomCheckoutConstants.PAY_API;

		PgPaymentRequest request = PgPaymentRequest.TokenPayRequestBuilder()
				.merchantOrderId("ORDER_PCI_TOKEN_001")
				.amount(3000L)
				.encryptionKeyId(2L)
				.authMode("H2H")
				.cryptogram("cryptogram123")
				.encryptedToken("encToken789")
				.encryptedCvv("encCvv456")
				.panSuffix("1234")
				.expiryMonth("06")
				.expiryYear("2026")
				.cardHolderName("Token User")
				.redirectUrl("https://merchant.com/redirect")
				.build();

		PgPaymentResponse response = PgPaymentResponse.builder()
				.orderId("OMO_PCI_TOKEN_001")
				.state("PENDING")
				.expireAt(java.time.Instant.now().getEpochSecond() + 600)
				.build();

		addStubForPostRequest(url, getHeaders(), request, HttpStatus.SC_OK, Maps.newHashMap(),
				response);

		PgPaymentResponse actual = customCheckoutClient.pay(request);
		Assertions.assertEquals(response.getOrderId(), actual.getOrderId());
	}

	// ── Non-PCI instrument uses default host ──────────────────────────────

	@Test
	void testUpiCollectUsesDefaultHost() {
		final String url = CustomCheckoutConstants.PAY_API;

		PgPaymentRequest request = PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
				.merchantOrderId("ORDER_NON_PCI_001")
				.amount(1000L)
				.vpa("user@upi")
				.build();

		PgPaymentResponse response = PgPaymentResponse.builder()
				.orderId("OMO_NON_PCI_001")
				.state("PENDING")
				.expireAt(java.time.Instant.now().getEpochSecond() + 600)
				.build();

		addStubForPostRequest(url, getHeaders(), request, HttpStatus.SC_OK, Maps.newHashMap(),
				response);

		PgPaymentResponse actual = customCheckoutClient.pay(request);
		Assertions.assertEquals(response.getOrderId(), actual.getOrderId());
	}

	// ── DeviceOS header injection ─────────────────────────────────────────

	@Test
	void testPayInjectsDeviceOsHeaderWhenPresent() {
		final String url = CustomCheckoutConstants.PAY_API;

		PgPaymentRequest request = PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
				.merchantOrderId("ORDER_DEVICE_OS_001")
				.amount(1000L)
				.vpa("user@upi")
				.deviceOS("ANDROID")
				.build();

		PgPaymentResponse response = PgPaymentResponse.builder()
				.orderId("OMO_DEVICE_OS_001")
				.state("PENDING")
				.expireAt(java.time.Instant.now().getEpochSecond() + 600)
				.build();

		Map<String, String> headersWithDeviceOs = ImmutableMap.<String, String>builder()
				.putAll(getHeaders())
				.put(Headers.X_DEVICE_OS, "ANDROID")
				.build();

		addStubForPostRequest(url, headersWithDeviceOs, request, HttpStatus.SC_OK,
				Maps.newHashMap(), response);

		PgPaymentResponse actual = customCheckoutClient.pay(request);
		Assertions.assertEquals(response.getOrderId(), actual.getOrderId());
	}

	@Test
	void testPayDoesNotInjectDeviceOsHeaderWhenAbsent() {
		final String url = CustomCheckoutConstants.PAY_API;

		PgPaymentRequest request = PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
				.merchantOrderId("ORDER_NO_DEVICE_OS_001")
				.amount(1000L)
				.vpa("user@upi")
				.build();

		PgPaymentResponse response = PgPaymentResponse.builder()
				.orderId("OMO_NO_DEVICE_OS_001")
				.state("PENDING")
				.expireAt(java.time.Instant.now().getEpochSecond() + 600)
				.build();

		// Stub only with base headers — if x-device-os were sent, WireMock would not match
		addStubForPostRequest(url, getHeaders(), request, HttpStatus.SC_OK, Maps.newHashMap(),
				response);

		PgPaymentResponse actual = customCheckoutClient.pay(request);
		Assertions.assertEquals(response.getOrderId(), actual.getOrderId());
	}
}
