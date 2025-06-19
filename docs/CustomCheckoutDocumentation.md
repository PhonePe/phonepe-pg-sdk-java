# PhonePe B2B PG SDK

------

## Installation

Requirements:

1) Java 17 or later

### Maven users

Add the dependency to your project's POM file:

```xml

<dependency>
    <groupId>com.phonepe</groupId>
    <artifactId>pg-sdk-java</artifactId>
    <version>2.1.3</version>
</dependency>
```

### Gradle users

Add the following to your project's build.gradle file.
Include the pg-sdk-java JAR in your dependencies.

```java
dependencies {
    implementation 'com.phonepe:pg-sdk-java:2.1.3'
}
```

-----

## Quick start:

To get your keys, please visit the Merchant Onboarding of PhonePe
PG: [Merchant Onboarding](https://developer.phonepe.com/v1/docs/merchant-onboarding)

You will need three things to get started: `clientId`, `clientSecret` & `clientVersion`

### [Class Initialisation](#class-initialization)

To create an instance of the `CustomCheckoutClient` class, you need to provide the keys received at the time of
onboarding.

Example:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);
```

### [Initiate an order](#pay-instruments)

To init a pay request, we make a request object using `PgPaymentRequest` builder. Multiple builders are
implemented according to the instruments.

You will get to initiate the order using the `pay()` function: [PAY](#pay-function)

### [1. UPI_INTENT](#1-using-upi_intent)

##### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String deviceOS = "IOS";
String targetApp = "PHONEPE";

PgPaymentRequest pgPaymentRequest = PgPaymentRequest.UpiIntentPayRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .targetApp(targetApp)
        .deviceOS(deviceOS)
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
String intentUrl = pgPaymentResponse.getIntentUrl();
```

The response object will be [PgPaymentResponse](#response-properties-for-upi_intent). Extract the `intentUrl`
from
the response received

### [2. UPI_COLLECT_VIA_VPA](#2-using-upi_collect_via_vpa)

Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String deviceOS = "IOS";
String vpa = "<REPLACE_WITH_REQUIRED_VPA>";

PgPaymentRequest pgPaymentRequest = PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
        .vpa(vpa)
        .amount(amount)
        .deviceOS(deviceOS)
        .merchantOrderId(merchantOrderId)
        .message("collect_message")
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
```

The response object will be [PgPaymentResponse](#response-properties-for-upi_collect_via_vpa). It will raise a
collect request on above-mentioned `vpa`

### [3. UPI_QR](#3-using-upi_qr)

Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String deviceOS = "IOS";
PgPaymentRequest pgPaymentRequest = PgPaymentRequest.UpiQrRequestBuilder()
        .amount(amount)
        .deviceOS(deviceOS)
        .merchantOrderId(merchantOrderId)
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
String qrData = pgPaymentResponse.getQrData();
```

The response object will be [PgPaymentResponse](#response-properties-for-upi_qr). Extract the `qrData` from the
response received

----

### [Create Sdk Order Integration](#create-sdk-order)

The `createSdkOrder()` function is used to create an order

Example:

```java
import com.phonepe.sdk.pg.common.models.request.AccountConstraint;
import java.util.Arrays;
import java.util.UUID;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String accountNumber = "<ACCOUNT_NUMBER>";
String ifsc = "<IFSC_CODE>";

AccountConstraint constraints = AccountConstraint.builder()
        .accountNumber(accountNumber)
        .ifsc(ifsc)
        .build();

CreateSdkOrderRequest createSdkOrderRequest = CreateSdkOrderRequest.CustomCheckoutBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .constraints(Arrays.asList(constraints))
        .build();

CreateSdkOrderResponse createSdkOrderResponse = customCheckoutClient.createSdkOrder(createSdkOrderRequest);
String token = createSdkOrderResponse.getToken();
```

Merchant should retrieve the token from the response object [CreateSdkOrderResponse](#createsdkorderresponse-properties)

-----

### [Check Status of an order](#order-status)

View the state for the order we just initiated.

Example:

```java
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;

String merchantOrderId = "<merchantOrderId>";  //created at the time of order creation

OrderStatusResponse orderStatusResponse = customCheckoutClient.getOrderStatus(merchantOrderId);
String state = orderStatusResponse.getState();
```

The function returns a [OrderStatusResponse](#orderstatusresponse-properties) object from which merchant can
retrieve the status of the order.
----

### [Callback Handling](#callback-verification)

You will receive a callback you have configured. The merchant have to call the `validateCallback` function.

It is important to check the validity of the callback received from PhonePe using the validate_callback function.

Example:

```java
import com.phonepe.sdk.pg.common.models.response.CallbackResponse;

String username = "<username>";
String password = "<password>";
String authorization = "<authorization";
String responseBody = "<responseBody>";

CallbackResponse callbackResponse = customCheckoutClient.validateCallback(username, password, authorization,
        responseBody);
String orderId = callbackResponse.getPayload()
        .getOrderId();
String state = callbackResponse.getPayload()
        .getState();
```

The `validateCallback()` function returns a [CallbackResponse](#callbackresponse), if the callback is valid, otherwise a
`PhonePeException` thrown.

---

## PhonePe PG JAVA SDK

### [Class Initialization](#class-initialisation)

CustomCheckoutClient class will be used to communicate with the PhonePe APIs. You can initiate the instance of this
class only once.
Use required credentials while initializing the object.

Disclaimer: For production builds don't save credentials in code.

#### Parameters:

| Parameter      | Type      | Mandatory | Description                                                                                       |
|----------------|-----------|-----------|---------------------------------------------------------------------------------------------------|
| `clientId`     | `String`  | Yes       | Unique Client ID provided by PhonePe.                                                             |
| `clientSecret` | `String`  | Yes       | Unique Client Secret provided by PhonePe.                                                         |
| `cientVersion` | `Integer` | Yes       | Unique Client Version provided by PhonePe.                                                        |
| `env`          | `Env`     | Yes       | Environment for the CustomCheckoutClient: `Env.PRODUCTION` (production), `Env.SANDBOX` (testing). |

#### Throws PhonePeException:

If another `CustomCheckoutClient` object is initialized, `PhonePeException` is thrown.

#### Example:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;   //insert your client version here
Env env = Env.SANDBOX;       //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);
```

-----

## [PAY INSTRUMENTS](#initiate-an-order)

The `pay()` method is used to initiate an order via various instruments.

### [1. Using UPI_INTENT](#1-upi_intent)

### Parameters:

| Parameter                | Type                                                  | Mandatory | Description                                                                                                            | 
|--------------------------|-------------------------------------------------------|-----------|------------------------------------------------------------------------------------------------------------------------|
| `merchantOrderId`        | `String`                                              | Yes       | Unique order ID generated by merchant                                                                                  |
| `amount`                 | `long`                                                | Yes       | Amount of order in Paisa                                                                                               |
| `contraints`             | List<[InstrumentConstraint](#instrument-constraints)> | No        | Different type of constraints that must be applied to the payment	                                                     |
| `deviceOS`               | `String`                                              | No        | Operating system of the device. Allowed values are:<br/>1. `IOS` <br/>2. `ANDROID`                                     |
| `merchantCallbackScheme` | `String`                                              | No        | Required only in case targetApp = `PHONEPE` and deviceOS = `IOS`                                                       |
| `targetApp`              | `String`                                              | No        | Target app through which the payment has to process. Allowed values are:<br/>1. `PHONEPE`<br/>2. `PAYTM`<br/>3. `GPAY` |

### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import java.util.UUID;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String deviceOS = "IOS";
String targetApp = "PHONEPE";

PgPaymentRequest pgPaymentRequest = PgPaymentRequest.UpiIntentPayRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .targetApp(targetApp)
        .deviceOS(deviceOS)
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
String intentUrl = pgPaymentResponse.getIntentUrl();
```

Extract the `intentUrl` from the response received

### Response Properties For UPI_INTENT

| Property    | Type     | Description                                                     |
|-------------|----------|-----------------------------------------------------------------|
| `orderId`   | `String` | Unique order ID generated by PhonePe                            |
| `state`     | `String` | State of the order initiated. Initially it will be `PENDING`    |
| `expireAt`  | `long`   | Expire time in epoch	                                           |
| `intentUrl` | `String` | Intent url according to the targetApp mentioned in the request	 | 

### [2. Using UPI_COLLECT_VIA_VPA](#2-upi_collect_via_vpa)

### Parameters:

| Parameter         | Type                                                  | Mandatory | Description                                                        |
|-------------------|-------------------------------------------------------|-----------|--------------------------------------------------------------------|
| `merchantOrderId` | `String`                                              | Yes       | Unique order ID generated by merchant                              |
| `amount`          | `long`                                                | Yes       | Amount of order in Paisa                                           |
| `contraints`      | List<[InstrumentConstraint](#instrument-constraints)> | No        | Different type of constraints that must be applied to the payment	 |
| `vpa`             | `String`                                              | Yes       | VPA against which collect request need to be raised                |

### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import java.util.UUID;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String vpa = "<VALID_VPA>";
PgPaymentRequest pgPaymentRequest = PgPaymentRequest.UpiCollectPayViaVpaRequestBuilder()
        .vpa(vpa)
        .amount(amount)
        .merchantOrderId(merchantOrderId)
        .message("Collect Message")
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
```

The function will raise the collect request to the above mentioned `vpa`

### Response Properties For UPI_COLLECT_VIA_VPA

| Property   | Type     | Description                                                  |
|------------|----------|--------------------------------------------------------------|
| `orderId`  | `String` | Unique order ID generated by PhonePe                         |
| `state`    | `String` | State of the order initiated. Initially it will be `PENDING` |
| `expireAt` | `long`   | Expire time in epoch	                                        |

### [3. Using UPI_QR](#3-upi_qr)

### Parameters:

| Parameter         | Type                                                  | Mandatory | Description                                                        |
|-------------------|-------------------------------------------------------|-----------|--------------------------------------------------------------------|
| `merchantOrderId` | `String`                                              | Yes       | Unique order ID generated by merchant                              |
| `amount`          | `long`                                                | Yes       | Amount of order in Paisa                                           |
| `contraints`      | List<[InstrumentConstraint](#instrument-constraints)> | No        | Different type of constraints that must be applied to the payment	 |

### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import java.util.UUID;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;

PgPaymentRequest pgPaymentRequest = PgPaymentRequest.UpiQrRequestBuilder()
        .amount(amount)
        .merchantOrderId(merchantOrderId)
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
String qrData = pgPaymentResponse.getQrData();
```

Extract the `qrData` from the response received

### Response Properties For UPI_QR

| Property    | Type     | Description                                                  |
|-------------|----------|--------------------------------------------------------------|
| `orderId`   | `String` | Unique order ID generated by PhonePe                         |
| `state`     | `String` | State of the order initiated. Initially it will be `PENDING` |
| `expireAt`  | `long`   | Expire time in epoch	                                        |
| `intentUrl` | `String` | Intent url for the amount mentioned	                         |
| `qrData`    | `String` | QR Data which will be used to generate the QR                |

### [4. Using NET_BANKING](#5-net_banking)

### Parameters:

| Parameter         | Type                                                  | Mandatory | Description                                                        |
|-------------------|-------------------------------------------------------|-----------|--------------------------------------------------------------------|
| `merchantOrderId` | `String`                                              | Yes       | Unique order ID generated by merchant                              |
| `amount`          | `long`                                                | Yes       | Amount of order in Paisa                                           |
| `contraints`      | List<[InstrumentConstraint](#instrument-constraints)> | No        | Different type of constraints that must be applied to the payment	 |
| `bankId`          | `String`                                              | Yes       | Bank id from where payment will be completed.                      |

### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import java.util.UUID;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String bankId = "HDFC";

PgPaymentRequest pgPaymentRequest = PgPaymentRequest.NetBankingPayRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .bankId(bankId)
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
String redirectUrl = pgPaymentResponse.getRedirectUrl();
```

Extract the `redirectUrl` from the response received

### Response Properties For NET_BANKING:

| Property      | Type     | Description                                                    |
|---------------|----------|----------------------------------------------------------------|
| `orderId`     | `String` | Unique order ID generated by PhonePe                           |
| `state`       | `String` | State of the order initiated. Initially it will be `PENDING`   |
| `expireAt`    | `long`   | Expire time in epoch	                                          |
| `redirectUrl` | `String` | Redirect url to perform the Net Banking for mentioned `bankId` |

### [5. Using TOKEN](#6-token)

### Parameters:

| Parameter          | Type                                                  | Mandatory | Description                                                                                           |
|--------------------|-------------------------------------------------------|-----------|-------------------------------------------------------------------------------------------------------|
| `merchantOrderId`  | `String`                                              | Yes       | Unique order ID generated by merchant                                                                 |
| `amount`           | `long`                                                | Yes       | Amount of order in Paisa                                                                              |
| `contraints`       | List<[InstrumentConstraint](#instrument-constraints)> | No        | Different type of constraints that must be applied to the payment	                                    |
| `authMode`         | `String`                                              | Yes       | Default to `3DS`                                                                                      |
| `encryptionKeyId`  | `long`                                                | Yes       | KeyId of key which merchant uses to encrypt card number & cvv                                         |
| `encryptedToken`   | `String`                                              | Yes       | `Encrypted TOKEN` number which merchant passes to process card transaction                            |
| `encryptedCvv `    | `String`                                              | Yes       | `Encrypted CVV` of the card with which payment is being initiated.                                    |
| `cryptogram`       | `String`                                              | Yes       | The `cryptogram` fetched from the gateway where the card was tokenized.                               |
| `panSuffix `       | `String`                                              | Yes       | Last four digits of cardNumber                                                                        |
| `cardHolderName  ` | `String`                                              | Yes       | Card Holder Name                                                                                      |
| `expiryMonth `     | `String`                                              | Yes       | Token expiry month                                                                                    |
| `expiryYear`       | `String`                                              | Yes       | Token expiry year                                                                                     |
| `merchantUserId `  | `String`                                              | No        | The unique identifier of the merchant user. It is used to associate the payment with a specific user. |

### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import java.util.UUID;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
long encryptionKeyId = 10;
String encryptedCvv = "<encryptedCvv>";
String authMode = "3DS";
String panSuffix = "7239";
String cryptogram = "<cryptogram>";
String encryptedToken = "<encryptedToken>";
String merchantUserId = "<merchantUserId>";
String cardHolderName = "<cardHolderName>";
String expiryYear = "2050";
String expiryMonth = "08";

PgPaymentRequest pgPaymentRequest = PgPaymentRequest.TokenPayRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .encryptionKeyId(encryptionKeyId)
        .encryptedCvv(encryptedCvv)
        .authMode(authMode)
        .panSuffix(panSuffix)
        .cryptogram(cryptogram)
        .encryptedToken(encryptedToken)
        .merchantUserId(merchantUserId)
        .cardHolderName(cardHolderName)
        .expiryMonth(expiryMonth)
        .expiryYear(expiryYear)
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
String redirectUrl = pgPaymentResponse.getRedirectUrl();
```

Extract the `redirectUrl` from the response received

### Response Properties For TOKEN

| Property      | Type     | Description                                                  |
|---------------|----------|--------------------------------------------------------------|
| `orderId`     | `String` | Unique order ID generated by PhonePe                         |
| `state`       | `String` | State of the order initiated. Initially it will be `PENDING` |
| `expireAt`    | `long`   | Expire time in epoch	                                        |
| `redirectUrl` | `String` | Redirect url to perform the transaction                      |

### [6. Using CARD](#7-card)

### Parameters

| Parameter             | Type                                                  | Mandatory | Description                                                                                           |
|-----------------------|-------------------------------------------------------|-----------|-------------------------------------------------------------------------------------------------------|
| `merchantOrderId`     | `String`                                              | Yes       | Unique order ID generated by merchant                                                                 |
| `amount`              | `long`                                                | Yes       | Amount of order in Paisa                                                                              |
| `contraints`          | List<[InstrumentConstraint](#instrument-constraints)> | No        | Different type of constraints that must be applied to the payment	                                    |
| `authMode`            | `String`                                              | Yes       | Default to `3DS`                                                                                      |
| `encryptionKeyId`     | `long`                                                | Yes       | KeyId of key which merchant uses to encrypt card number & cvv                                         |
| `encryptedCardNumber` | `String`                                              | Yes       | Encrypted 16-Digit Card Number entered by the user.                                                   |
| `cardHolderName  `    | `String`                                              | No        | Card Holder Name                                                                                      |
| `encryptedCvv `       | `String`                                              | Yes       | `Encrypted CVV` of the card with which payment is being initiated.                                    |
| `expiryMonth `        | `String`                                              | Yes       | Token expiry month                                                                                    |
| `expiryYear`          | `String`                                              | Yes       | Token expiry year                                                                                     |
| `merchantUserId `     | `String`                                              | No        | The unique identifier of the merchant user. It is used to associate the payment with a specific user. |

### Example:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
long encryptionKeyId = 10;
String authMode = "3DS";
String encryptedCardNumber = "<encryptedCardNumber>";
String encryptedCvv = "<encryptedCvv>";
String merchantUserId = "<merchantUserId>";
String cardHolderName = "<cardHolderName>";
String expiryYear = "2052";
String expiryMonth = "08";

PgPaymentRequest pgPaymentRequest = PgPaymentRequest.CardPayRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .encryptionKeyId(encryptionKeyId)
        .encryptedCvv(encryptedCvv)
        .authMode(authMode)
        .encryptedCardNumber(encryptedCardNumber)
        .merchantUserId(merchantUserId)
        .cardHolderName(cardHolderName)
        .expiryMonth(expiryMonth)
        .expiryYear(expiryYear)
        .build();

PgPaymentResponse pgPaymentResponse = customCheckoutClient.pay(pgPaymentRequest);
String redirectUrl = pgPaymentResponse.getRedirectUrl();
```

### Response Properties For Card

| Property      | Type     | Description                                                  |
|---------------|----------|--------------------------------------------------------------|
| `orderId`     | `String` | Unique order ID generated by PhonePe                         |
| `state`       | `String` | State of the order initiated. Initially it will be `PENDING` |
| `expireAt`    | `long`   | Expire time in epoch	                                        |
| `redirectUrl` | `String` | Redirect url to perform the transaction                      |

## [CREATE SDK ORDER](#create-sdk-order-integration)

The function `createSdkOrder()` is used to create an order

### Parameters:

| Parameter               | Type                    | Mandatory | Description                                                                                            |
|-------------------------|-------------------------|-----------|--------------------------------------------------------------------------------------------------------|
| `createSdkOrderRequest` | `CreateSdkOrderRequest` | Yes       | The request is built using [CreateSdkOrderRequest.CustomCheckoutBuilder()](#sdk-order-request-builder) |

### Sdk Order Request Builder

| Parameter         | Type                                                  | Mandatory | Description                                                        |
|-------------------|-------------------------------------------------------|-----------|--------------------------------------------------------------------|
| `merchantOrderId` | `String`                                              | Yes       | Unique order ID generated by merchant                              |
| `amount`          | `long`                                                | Yes       | Amount of order in Paisa                                           |
| `contraints`      | List<[InstrumentConstraint](#instrument-constraints)> | Yes       | Different type of constraints that must be applied to the payment	 |

### Example:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.request.AccountConstraint;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;
import java.util.Arrays;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<merchantOrderID>";
long amount = 100;
String accountNumber = "420200001892";
String ifsc = "ICIC0000041";

AccountConstraint constraints = AccountConstraint.builder()
        .ifsc(ifsc)
        .accountNumber(accountNumber)
        .build();

CreateSdkOrderRequest createSdkOrderRequest = CreateSdkOrderRequest.CustomCheckoutBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .constraints(Arrays.asList(constraints))
        .build();

CreateSdkOrderResponse createSdkOrderResponse = customCheckoutClient.createSdkOrder(createSdkOrderRequest);
String token = createSdkOrderResponse.getToken();
```

### Returns

The function returns a [CreateSdkOrderResponse](#createsdkorderresponse-properties) properties:

### CreateSdkOrderResponse Properties

| Property   | Type     | Description                                      |  
|------------|----------|--------------------------------------------------|  
| `orderId`  | `String` | Order ID generated by PhonePE                    |  
| `state`    | `String` | State of the Order. Initially it will be PENDING |  
| `expireAt` | `long`   | Expiry time in epoch                             |  
| `token`    | `String` | Token used to access the PG Page.                |  

-----

## [Order Status](#check-status-of-an-order)

It is used to retrieve the status of an order using `getOrderStatus()` function.

### Parameters:

| Parameter       | Type     | Mandatory | Description                                                                                                                                 |
|-----------------|----------|-----------|---------------------------------------------------------------------------------------------------------------------------------------------|
| merchantOrderId | `String` | Yes       | The merchant order ID for which the status is fetched.                                                                                      |
| details         | `String` | No        | 1. True → return all attempt details under paymentDetails list <br/>2. False → return only latest attempt details under paymentDetails list |

### Example:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<merchantOrderId>";

OrderStatusResponse orderStatusResponse = customCheckoutClient.getOrderStatus(merchantOrderId);
String state = orderStatusResponse.getState();

```

### Returns

The function returns a `OrderStatusResponse` object with the following properties:

### OrderStatusResponse Properties:

| Property         | Type                                             | Description                                                                                                         |  
|------------------|--------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|  
| `orderId`        | `String`                                         | Order ID created by PhonePe.                                                                                        |  
| `state`          | `String`                                         | State of the order. It can be in any one of the following states: <br/> 1. PENDING <br/> 2. FAILED<br/>3. COMPLETED |  
| `amount`         | `long`                                           | Order amount in Paise                                                                                               |  
| `expireAt`       | `long`                                           | Order expiry time in epoch                                                                                          |  
| `paymentDetails` | List<[PaymentDetail](#paymentdetail-properties)> | Contain list of details of each transaction attempt made corresponding to this particular order                     |  

### PaymentDetail Properties:

| Property            | Type                                        | Description                                                                                                                                               |
|---------------------|---------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `paymentMode`       | `String`                                    | Mode of Payment. It can be anyone of the following modes: <br/>1. UPI_INTENT<br/>2. UPI_COLLECT<br/>3. UPI_QR<br/>4. CARD<br/>5. TOKEN<br/>6. NET_BANKING |
| `timestamp`         | `long`                                      | Timestamp of the attempted transaction in epoch                                                                                                           |  
| `amount`            | `long`                                      | Order amount in Paisa                                                                                                                                     |
| `transactionId`     | `String`                                    | Transaction Id generated by the PhonePe                                                                                                                   |  
| `state`             | `String`                                    | Attempted transaction state. It can be any one of the following states: <br/>1. PENDING<br/>2. COMPLETED<br/>3. FAILED                                    |
| `errorCode`         | `String`                                    | Error code (Only present when transaction state is failed)                                                                                                |  
| `detailedErrorCode` | `String`                                    | Detailed Error Code (Only present when transaction state is failed)                                                                                       |  
| `rail`              | [PaymentRail](#paymentrail)                 | Contains processing rail details under which transaction attempt is made.                                                                                 |  
| `instrument`        | [PaymentInstrumentV2](#paymentinstrumentv2) | Contains instrument details of that particular transaction Id                                                                                             |
| `splitInstruments`  | List<[InstrumentCombo](#instrumentcombo)>   | Contains split instrument details of all the transactions made                                                                                            |

### InstrumentCombo

| Property     | Type                  | Description                                            |  
|--------------|-----------------------|--------------------------------------------------------|  
| `instrument` | `PaymentInstrumentV2` | Instrument used for the payment                        |  
| `rails`      | `PaymentRail`         | Rail used for the payment                              |  
| `amount`     | `long`                | Amount transferred using the above instrument and rail |

-----

## Transaction Status:

Checks the status of a transaction.

### Parameters:

| Parameter       | Type     | Mandatory | Description                                       |                                                                                                          
|-----------------|----------|-----------|---------------------------------------------------|
| `transactionId` | `String` | Yes       | The transaction attempt id received from PhonePe. |  

### Example

```java
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live
CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<merchantOrderId>";
boolean details = true;

OrderStatusResponse transactionStatusResponse = customCheckoutClient.getTransactionStatus(transactionId);
String state = transactionStatusResponse.getState();
```

### Returns

The function returns a [OrderStatusResponse](#orderstatusresponse-properties) mentioned in Check Status Function.

---

## [Callback Verification](#callback-handling)

You need to pass 4 parameters to the `validateCallback()` function

### Parameters:

| Parameter       | Type     | Mandatory | Description                                                     |
|-----------------|----------|-----------|-----------------------------------------------------------------|
| `username`      | `String` | Yes       | Unique username configured for the callback url                 |
| `password`      | `String` | Yes       | Unique password configured for the callback url                 |
| `authorization` | `String` | Yes       | Value of the `Authorization` header under the callback response |
| `responseBody`  | `String` | Yes       | Callback response body as string.                               |

#### Example usage

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.CallbackResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String username = "<username>";
String password = "<password>";
String authorization = "<authorization>";
String responseBody = "<responseBody>";

CallbackResponse callbackResponse = customCheckoutClient.validateCallback(username, password, authorization,
        responseBody);
String callbackType = callbackResponse.getType();
String orderId = callbackResponse.getPayload()
        .getOrderId();
String state = callbackResponse.getPayload()
        .getState();
```

### Returns

The function returns a [CallbackResponse](#callbackresponse) if the callback is valid, otherwise throws
a `PhonePeException`

### CallbackResponse:

| Property  | Type                                     | Description                                             |  
|-----------|------------------------------------------|---------------------------------------------------------|  
| `type`    | [CallbackType](#callback-types)          | Contains type of callback received at the merchant end. |  
| `payload` | [CallbackData](#callbackdata-properties) | Contains callback details.                              |  

### Callback Types

| Callback Type                       | Context                                          |
|-------------------------------------|--------------------------------------------------|
| CHECKOUT_ORDER_COMPLETED            | Order Completed for checkout                     |
| CHECKOUT_ORDER_FAILED               | Order Failed                                     |
| CHECKOUT_TRANSACTION_ATTEMPT_FAILED | Transaction Attempt Failed for checkout          |
| PG_ORDER_COMPLETED                  | Order Completed for PG                           |
| PG_ORDER_FAILED                     | Order Failed for PG                              |
| PG_TRANSACTION_ATTEMPT_FAILED       | Transaction Attempt Failed for PG                |
| PG_REFUND_COMPLETED                 | Refund Completed for PG                          |
| PG_REFUND_ACCEPTED                  | Refund Accepted by PhonePe and will be initiated |
| PG_REFUND_FAILED                    | Refund Failed for PG                             |

### CallbackData Properties

| Property                  | Type                                         | Description                                                                                     |
|---------------------------|----------------------------------------------|-------------------------------------------------------------------------------------------------|
| `merchantId`              | `String`                                     | The merchant from which request was initiated                                                   |  
| `orderId`                 | `String`                                     | Order id generated by PhonePe (Only present in case of order callbacks)                         |
| `merchantOrderId`         | `String`                                     | Order id generated by merchant (Only present in case of order callbacks)                        |
| `originalMerchantOrderId` | `String`                                     | Order id generated by merchant (Only present in case of refund callback)                        |  
| `refundId`                | `String`                                     | Refund id generated by PhonePe (Only present in case of refund callback)                        |  
| `merchantRefundId`        | `String`                                     | Refund id generated by merchant (Only present in case of refund callback)                       |  
| `state`                   | `String`                                     | State of the ORDER/REFUND                                                                       |  
| `amount`                  | `long`                                       | Amount in Paisa of the order/refund processed                                                   |
| `expireAt`                | `long`                                       | Expiry in epoch                                                                                 |
| `errorCode`               | `String`                                     | Error code. (Only present when state is failed)                                                 |  
| `detailedErrorCode`       | `String`                                     | Detailed error code. (Only present when state is failed)                                        |  
| `metaInfo`                | `MetaInfo`                                   | Additional Information about the order                                                          |
| `paymentDetails`          | List<[PaymentDetail](#paymentdetail-object)> | Contain list of details of each transaction attempt made corresponding to this particular order |  

### PaymentDetail Object

| Property            | Type                                        | Description                                                                                                                                               |  
|---------------------|---------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|  
| `transactionId`     | `String`                                    | Transaction Id generated by the PhonePe                                                                                                                   |  
| `paymentMode`       | `String`                                    | Mode of Payment. It can be anyone of the following modes: <br/>1. UPI_INTENT<br/>2. UPI_COLLECT<br/>3. UPI_QR<br/>4. CARD<br/>5. TOKEN<br/>6. NET_BANKING |  
| `timestamp`         | `long`                                      | Timestamp of the attempted transaction in epoch                                                                                                           |  
| `state`             | `String`                                    | Attempted transaction state. It can be any one of the following states: <br/>1. PENDING<br/>2. COMPLETED<br/>3. FAILED                                    |  
| `amount`            | `long`                                      | Amount in Paisa of the order/refund processed                                                                                                             |
| `errorCode`         | `String`                                    | Error code present only when the transaction state is Failed                                                                                              |  
| `detailedErrorCode` | `String`                                    | Detailed Error Code present only when transaction state is Failed                                                                                         |  
| `rail`              | [PaymentRail](#paymentrail)                 | Contains processing rail details under which transaction attempt is made.                                                                                 |  
| `instrument`        | [PaymentInstrumentV2](#paymentinstrumentv2) | Contains instrument details of that particular transaction Id                                                                                             |
| `splitInstruments`  | List<[InstrumentCombo](#instrumentcombo)>   | Contains split instrument details of all the transactions made                                                                                            |

-----

## Refund

It is used to initiate a refund using `refund()` function

#### Parameters:

| Parameter       | Type            | Mandatory | Description                                                               |
|-----------------|-----------------|-----------|---------------------------------------------------------------------------|
| `refundRequest` | `RefundRequest` | Yes       | The request built using [RefundRequest](#refund-request-builder) builder. |

### Refund Request Builder

| Parameter                 | Type     | Mandatory | Description                                                 | Constrains                              |
|---------------------------|----------|-----------|-------------------------------------------------------------|-----------------------------------------|
| `merchantRefundId`        | `String` | Yes       | Unique merchant refund id generated by merchant             | Max Length = 63 characters              |
| `originalMerchantOrderId` | `String` | Yes       | Original merchant order id against which refund is required | -                                       |
| `amount`                  | `long`   | Yes       | Amount in paisa to refund                                   | Min Value = 1, Max Value = Order Amount |              

### Example

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.response.RefundResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantRefundId = UUID.randomUUID()
        .toString();
String originalMerchantOrderId = "<merchantOrderId>";
long amount = 100;

RefundRequest refundRequest = RefundRequest.Builder()
        .merchantRefundId(merchantRefundId)
        .originalMerchantOrderId(merchantOrderId)
        .amount(amount)
        .build();

RefundResponse refundResponse = customCheckoutClient.refund(refundRequest);
String state = refundResponse.getState();
```

### Returns

The function returns a `RefundResponse` Object

### Refund Response

| Property   | Type     | Description                                                     |
|------------|----------|-----------------------------------------------------------------|
| `refundId` | `String` | PhonePe generated internal refund id                            |
| `amount`   | `long`   | Amount in Paisa to refund                                       |
| `state`    | `String` | The state of the refund initiated. Initially it will be PENDING |

------

## Refund Status

It is used to retrieve the status of a refund using `getRefundStatus()` function.

### Parameters:

| Parameter | Type     | Mandatory | Description                                                            |
|-----------|----------|-----------|------------------------------------------------------------------------|
| refundId  | `String` | Yes       | Refund Id created by the merchant at the time of initiating the refund |

### Example

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.CustomCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.RefundStatusResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live
CustomCheckoutClient customCheckoutClient = CustomCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String refundId = "<refundId>";

RefundStatusResponse refundStatusResponse = customCheckoutClient.getRefundStatus(refundId);
String state = refundStatusResponse.getState();
```

### Returns:

It returns a `RefundStatusResponse` Object

### RefundStatusResponse

| Property                  | Type                                              | Description                                                                                          |  
|---------------------------|---------------------------------------------------|------------------------------------------------------------------------------------------------------|  
| `merchantId`              | `String`                                          | Merchant Id who initiated the refund                                                                 |  
| `merchantRefundId`        | `String`                                          | Refund Id created by the merchant at the time of refund initiation                                   |  
| `originalMerchantOrderId` | `String`                                          | Order Id for which refund has initiated. Created by the merchant at the time of order creation       |  
| `amount`                  | `long`                                            | Amount to refund                                                                                     |  
| `state`                   | `String`                                          | State of the refund                                                                                  |
| `paymentDetails`          | List<[PaymentRefundDetail](#paymentrefunddetail)> | Contains the list of details of each transaction attempt made corresponding to this particular order |

### PaymentRefundDetail

| Property            | Type                                        | Description                                                                                                                                               |  
|---------------------|---------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|  
| `transactionId`     | `String`                                    | Transaction Id generated by the PhonePe                                                                                                                   |  
| `paymentMode`       | `String`                                    | Mode of Payment. It can be anyone of the following modes: <br/>1. UPI_INTENT<br/>2. UPI_COLLECT<br/>3. UPI_QR<br/>4. CARD<br/>5. TOKEN<br/>6. NET_BANKING |  
| `timestamp`         | `long`                                      | Timestamp of the attempted transaction in epoch                                                                                                           |  
| `state`             | `String`                                    | Attempted transaction state. It can be any one of the following states: <br/>1. PENDING<br/>2. COMPLETED<br/>3. FAILED                                    |  
| `errorCode`         | `String`                                    | Error code present only when the transaction state is Failed                                                                                              |  
| `detailedErrorCode` | `String`                                    | Detailed Error Code present only when transaction state is Failed                                                                                         |  
| `rail`              | [PaymentRail](#paymentrail)                 | Contains processing rail details under which transaction attempt is made.                                                                                 |  
| `instrument`        | [PaymentInstrumentV2](#paymentinstrumentv2) | Contains instrument details of that particular transaction Id                                                                                             |
| `splitInstruments`  | List<[InstumentCombo](#instrumentcombo)>    | Type of transaction instrument. It can be any one of the following types:<br/>1. ACCOUNT<br/>2. CREDIT_CARD<br/>3. DEBIT_CARD<br/>4. NET_BANKING          |

-----

## Response Models for Reference

### PaymentRail

Different types of rail which is used to initiate payment. The object is

###### UPI RAIL

| Property           | Type              | 
|--------------------|-------------------|
| `type`             | `PaymentRailType` |
| `utr`              | `String`          |
| `upiTransactionId` | `String`          |
| `vpa`              | `String`          |

###### PG RAIL

| Property               | Type              | 
|------------------------|-------------------|
| `type`                 | `PaymentRailType` |
| `transctionId`         | `String`          |
| `authorizationCode`    | `String`          |
| `serviceTransactionId` | `String`          |

###### PPI_WALLET RAIL

| Property | Type              | 
|----------|-------------------|
| `type`   | `PaymentRailType` |

###### PPI_EGV RAIL

| Property | Type              | 
|----------|-------------------|
| `type`   | `PaymentRailType` |

### PaymentInstrumentV2

Different types of instruments which is used to initiate a payment

###### ACCOUNT

| Property              | Type                    | 
|-----------------------|-------------------------|
| `type`                | `PaymentInstrumentType` |
| `ifsc`                | `String`                |
| `acountType`          | `String`                |
| `maskedAccountNumber` | `String`                |
| `accountHolderName`   | `String`                |

###### CREDIT_CARD

| Property            | Type                    | 
|---------------------|-------------------------|
| `type`              | `PaymentInstrumentType` |
| `bankTransactionId` | `String`                |
| `bankId`            | `String`                |
| `arn`               | `String`                |
| `brn`               | `String`                |

###### DEBIT_CARD

| Property            | Type                    | 
|---------------------|-------------------------|
| `type`              | `PaymentInstrumentType` |
| `bankTransactionId` | `String`                |
| `bankId`            | `String`                |
| `arn`               | `String`                |
| `brn`               | `String`                |

###### NET_BANKING

| Property            | Type                    | 
|---------------------|-------------------------|
| `type`              | `PaymentInstrumentType` |
| `bankTransactionId` | `String`                |
| `bankId`            | `String`                |
| `arn`               | `String`                |
| `brn`               | `String`                |

###### EGV

| Property     | Type                    | 
|--------------|-------------------------|
| `type`       | `PaymentInstrumentType` |
| `cardNumber` | `String`                |
| `programId`  | `String`                |

###### WALLET

| Property   | Type                    | 
|------------|-------------------------|
| `type`     | `PaymentInstrumentType` |
| `walletId` | `String`                |

### Instrument Constraints:

Different type of constraints that must be applied to the payment. The object is created using
AccountConstraint.builder() for constraint.type = ACCOUNT

##### ACCOUNT

| Property        | Type      | 
|-----------------|-----------|
| `type`          | `Account` |
| `accountNumber` | `String`  |
| `ifsc`          | `String`  |


