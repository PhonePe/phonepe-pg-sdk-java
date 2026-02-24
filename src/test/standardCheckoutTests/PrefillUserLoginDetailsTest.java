package standardCheckoutTests;/*
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
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.payments.v2.models.request.PrefillUserLoginDetails;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Unit tests for PrefillUserLoginDetails feature.
 * Tests cover:
 * - PrefillUserLoginDetails class functionality
 * - Integration with StandardCheckoutPayRequest
 * - JSON serialization/deserialization
 * - Edge cases and null handling
 */
class PrefillUserLoginDetailsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String redirectUrl = "https://merchant.com/callback";
    private final String merchantOrderId = "ORDER_" + System.currentTimeMillis();
    private final long amount = 10000L; // 100 INR

    // ========== PrefillUserLoginDetails Class Tests ==========

    @Test
    @DisplayName("Should create PrefillUserLoginDetails with valid phone number using builder")
    void testCreatePrefillUserLoginDetailsWithValidPhoneNumber() {
        // Arrange & Act
        PrefillUserLoginDetails prefill = PrefillUserLoginDetails.builder()
                .phoneNumber("9876543210")
                .build();

        // Assert
        assertNotNull(prefill);
        assertEquals("9876543210", prefill.getPhoneNumber());
    }

    @Test
    @DisplayName("Should create PrefillUserLoginDetails with null phone number")
    void testCreatePrefillUserLoginDetailsWithNullPhoneNumber() {
        // Arrange & Act
        PrefillUserLoginDetails prefill = PrefillUserLoginDetails.builder()
                .phoneNumber(null)
                .build();

        // Assert
        assertNotNull(prefill);
        assertNull(prefill.getPhoneNumber());
    }

    // ========== JSON Serialization Tests ==========

    @Test
    @DisplayName("Should serialize PrefillUserLoginDetails to JSON with phone number")
    void testSerializePrefillUserLoginDetailsWithPhoneNumber() throws Exception {
        // Arrange
        PrefillUserLoginDetails prefill = PrefillUserLoginDetails.builder()
                .phoneNumber("9876543210")
                .build();

        // Act
        String json = objectMapper.writeValueAsString(prefill);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("phoneNumber"));
        assertTrue(json.contains("9876543210"));
        assertEquals("{\"phoneNumber\":\"9876543210\"}", json);
    }

    // ========== StandardCheckoutPayRequest Integration Tests ==========

    @Test
    @DisplayName("Should create StandardCheckoutPayRequest with prefillUserLoginDetails")
    void testStandardCheckoutPayRequestWithPrefillUserLoginDetails() {
        // Arrange
        PrefillUserLoginDetails prefill = PrefillUserLoginDetails.builder()
                .phoneNumber("9876543210")
                .build();

        // Act
        StandardCheckoutPayRequest request = StandardCheckoutPayRequest.builder()
                .merchantOrderId(merchantOrderId)
                .amount(amount)
                .redirectUrl(redirectUrl)
                .prefillUserLoginDetails(prefill)
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(merchantOrderId, request.getMerchantOrderId());
        assertEquals(amount, request.getAmount());
        assertNotNull(request.getPrefillUserLoginDetails());
        assertEquals("9876543210", request.getPrefillUserLoginDetails().getPhoneNumber());
    }

    @Test
    @DisplayName("Should create StandardCheckoutPayRequest without prefillUserLoginDetails")
    void testStandardCheckoutPayRequestWithoutPrefillUserLoginDetails() {
        // Arrange & Act
        StandardCheckoutPayRequest request = StandardCheckoutPayRequest.builder()
                .merchantOrderId(merchantOrderId)
                .amount(amount)
                .redirectUrl(redirectUrl)
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(merchantOrderId, request.getMerchantOrderId());
        assertEquals(amount, request.getAmount());
        assertNull(request.getPrefillUserLoginDetails());
    }

    @Test
    @DisplayName("Should create StandardCheckoutPayRequest with null prefillUserLoginDetails")
    void testStandardCheckoutPayRequestWithNullPrefillUserLoginDetails() {
        // Arrange & Act
        StandardCheckoutPayRequest request = StandardCheckoutPayRequest.builder()
                .merchantOrderId(merchantOrderId)
                .amount(amount)
                .redirectUrl(redirectUrl)
                .prefillUserLoginDetails(null)
                .build();

        // Assert
        assertNotNull(request);
        assertNull(request.getPrefillUserLoginDetails());
    }

    @Test
    @DisplayName("Should serialize StandardCheckoutPayRequest with prefillUserLoginDetails to JSON")
    void testSerializeStandardCheckoutPayRequestWithPrefillUserLoginDetails() throws Exception {
        // Arrange
        PrefillUserLoginDetails prefill = PrefillUserLoginDetails.builder()
                .phoneNumber("9876543210")
                .build();

        StandardCheckoutPayRequest request = StandardCheckoutPayRequest.builder()
                .merchantOrderId(merchantOrderId)
                .amount(amount)
                .redirectUrl(redirectUrl)
                .prefillUserLoginDetails(prefill)
                .build();

        // Act
        String json = objectMapper.writeValueAsString(request);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("prefillUserLoginDetails"));
        assertTrue(json.contains("phoneNumber"));
        assertTrue(json.contains("9876543210"));
    }

    // ========== Edge Cases and Validation Tests ==========

    @Test
    @DisplayName("Should handle empty string phone number")
    void testPrefillUserLoginDetailsWithEmptyStringPhoneNumber() {
        // Arrange & Act
        PrefillUserLoginDetails prefill = PrefillUserLoginDetails.builder()
                .phoneNumber("")
                .build();

        // Assert
        assertNotNull(prefill);
        assertEquals("", prefill.getPhoneNumber());
    }

    // ========== Backward Compatibility Tests ==========

    @Test
    @DisplayName("Should maintain backward compatibility - request without prefillUserLoginDetails works")
    void testBackwardCompatibilityWithoutPrefillUserLoginDetails() {
        // This test ensures existing code continues to work
        // Act
        StandardCheckoutPayRequest request = StandardCheckoutPayRequest.builder()
                .merchantOrderId(merchantOrderId)
                .amount(amount)
                .redirectUrl(redirectUrl)
                .message("Test payment")
                .disablePaymentRetry(false)
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(merchantOrderId, request.getMerchantOrderId());
        assertEquals(amount, request.getAmount());
        assertNull(request.getPrefillUserLoginDetails());
        assertNotNull(request.getPaymentFlow());
    }

    @Test
    @DisplayName("Should work with all other StandardCheckoutPayRequest parameters")
    void testPrefillUserLoginDetailsWithAllOtherParameters() {
        // Arrange
        PrefillUserLoginDetails prefill = PrefillUserLoginDetails.builder()
                .phoneNumber("9876543210")
                .build();

        // Act
        StandardCheckoutPayRequest request = StandardCheckoutPayRequest.builder()
                .merchantOrderId(merchantOrderId)
                .amount(amount)
                .redirectUrl(redirectUrl)
                .message("Test payment message")
                .expireAfter(3600L) // 1 hour
                .disablePaymentRetry(true)
                .prefillUserLoginDetails(prefill)
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(merchantOrderId, request.getMerchantOrderId());
        assertEquals(amount, request.getAmount());
        assertEquals(3600L, request.getExpireAfter());
        assertTrue(request.getDisablePaymentRetry());
        assertNotNull(request.getPrefillUserLoginDetails());
        assertEquals("9876543210", request.getPrefillUserLoginDetails().getPhoneNumber());
    }
}
