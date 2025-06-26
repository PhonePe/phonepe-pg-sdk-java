# PhonePe B2B Payment Gateway SDK for Java

[![Maven Central](https://img.shields.io/badge/Maven%20Central-v2.1.3-blue)](https://maven-badges.herokuapp.com/maven-central/com.phonepe/pg-sdk-java)
![Java](https://img.shields.io/badge/Java-8%2B-orange)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

A Java library for seamless integration with PhonePe Payment Gateway APIs.

## Table of Contents
- [Requirements](#requirements)
- [Installation](#installation)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Quick Start](#quick-start)
  - [Initialization](#initialization)
  - [Standard Checkout Flow](#standard-checkout-flow)
  - [Checking Order Status](#checking-order-status)
  - [Handling Callbacks](#handling-callbacks)
  - [SDK Order Integration](#sdk-order-integration)
- [Documentation](#documentation)
- [License](#license)

## Requirements

- Java 8 or later
- Maven or Gradle build system

## Installation

### Maven

Add the dependency to your project's POM file:

```xml
<dependency>
    <groupId>com.phonepe</groupId>
    <artifactId>pg-sdk-java</artifactId>
    <version>2.1.3</version>
</dependency>
```

### Gradle

Add the following to your project's build.gradle file:

```gradle
dependencies {
    implementation 'com.phonepe:pg-sdk-java:2.1.3'
}
```

## Quick Start

### Initialization

Before using the SDK, you need to acquire your credentials from the [PhonePe Merchant Portal](https://developer.phonepe.com/v1/docs/merchant-onboarding).

You need three key pieces of information:
1. `clientId` - Your merchant identifier
2. `clientSecret` - Your authentication secret
3. `clientVersion` - API version to use

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;

String clientId = "<your-client-id>";
String clientSecret = "<your-client-secret>";
Integer clientVersion = 1;  // Your client version here
Env env = Env.SANDBOX;      // Use Env.PRODUCTION for live transactions

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(
    clientId, 
    clientSecret,
    clientVersion, 
    env
);
```

### Standard Checkout Flow

To initiate a payment, create a request using `StandardCheckoutPayRequest.builder()`:

```java
import java.util.UUID;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;

// Generate a unique order ID
String merchantOrderId = UUID.randomUUID().toString();
long amount = 10000;  // Amount in lowest currency denomination (paise for INR)
String redirectUrl = "https://www.yourwebsite.com/redirect";

StandardCheckoutPayRequest payRequest = StandardCheckoutPayRequest.builder()
    .merchantOrderId(merchantOrderId)
    .amount(amount)
    .redirectUrl(redirectUrl)
    .build();

StandardCheckoutPayResponse payResponse = standardCheckoutClient.pay(payRequest);
String checkoutPageUrl = payResponse.getRedirectUrl();

// Redirect the user to checkoutPageUrl to complete the payment
```

### Checking Order Status

To check the status of an order:

```java
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;

String merchantOrderId = "<your-merchant-order-id>";  // Order ID created during payment initialization
OrderStatusResponse orderStatusResponse = standardCheckoutClient.getOrderStatus(merchantOrderId);
String state = orderStatusResponse.getState();

// Handle the state accordingly in your application
```

### Handling Callbacks

PhonePe sends callbacks to your configured endpoint. Validate these callbacks to ensure they're authentic:

```java
import com.phonepe.sdk.pg.common.models.response.CallbackResponse;

// Credentials for Basic Authentication that you've configured in the PhonePe dashboard
String username = "<your-username>";
String password = "<your-password>";

// Data received in the callback
String authorization = request.getHeader("Authorization");  // Basic Authentication header
String responseBody = request.getBody();                   // JSON body as string

try {
    CallbackResponse callbackResponse = standardCheckoutClient.validateCallback(
        username, 
        password, 
        authorization,
        responseBody
    );
    
    String orderId = callbackResponse.getPayload().getOrderId();
    String state = callbackResponse.getPayload().getState();
    String event = callbackResponse.getEvent();
    
    // Process the order based on its state
} catch (PhonePeException e) {
    // Handle invalid callback - potential security issue
}
```

Possible callback states include:
- `checkout.order.completed` - Payment completed successfully
- `checkout.order.failed` - Payment failed
- `checkout.transaction.attempt.failed` - Transaction attempt failed

### SDK Order Integration

For mobile SDK integration, first create an order on your server:

```java
import java.util.UUID;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;

String merchantOrderId = UUID.randomUUID().toString();
long amount = 10000;  // Amount in lowest denomination (paise for INR)
String redirectUrl = "https://yourapp.com/callback";

CreateSdkOrderRequest orderRequest = CreateSdkOrderRequest.StandardCheckoutBuilder()
    .merchantOrderId(merchantOrderId)
    .amount(amount)
    .redirectUrl(redirectUrl)
    .build();

CreateSdkOrderResponse orderResponse = standardCheckoutClient.createSdkOrder(orderRequest);
String token = orderResponse.getToken();

// Pass this token to your mobile app to initiate payment through the PhonePe SDK
```

## Documentation

For detailed API documentation, advanced features, and integration options:

- [Standard Checkout Documentation](https://developer.phonepe.com/v1/reference/java-sdk-standard-checkout)
- [Subscription Documentation](https://developer.phonepe.com/v1/reference/java-sdk-introduction-autopay)
- [PhonePe Developer Portal](https://developer.phonepe.com/)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2025 PhonePe Private Limited

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```