# PhonePe B2B PG SDK

A Java library for integrating with PhonePe APIs

## Table of Contents
- [Installation](#installation)
- [Quick start](#quick-start)
  - [Initiate an order using Checkout Page](#initiate-an-order-using-checkout-page)
  - [Check Status of an order](#check-status-of-an-order)
  - [Order Callback Handling](#order-callback-handling)
- [Create Order SDK Integration](#create-order-sdk-integration)
- [PhonePe PG JAVA SDK](#phonepe-pg-java-sdk)
  - [Class Initialization](#class-initialization)
  - [Standard Checkout Pay](#standard-checkout-pay)
  - [Order Status](#order-status)
  - [Transaction Status](#transaction-status)
  - [Callback Verification](#callback-verification)
  - [Refund](#refund)
  - [Refund Status](#refund-status)
  - [SDK Order Creation](#sdk-order-creation)
  - [Exception Handling](#exception-handling)
- [Response Models for Reference](#response-models-for-reference)
  - [PaymentRail](#paymentrail)
  - [PaymentInstrumentV2](#paymentinstrumentv2)

-----

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

## Quick start

To get your keys, please visit the Merchant Onboarding of PhonePe
PG: [Merchant Onboarding](https://developer.phonepe.com/v1/docs/merchant-onboarding)

You will need three things to get started: `clientId`, `clientSecret` & `clientVersion`

Create an instance of the [StandardCheckoutClient](#class-initialization) class:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);
```

-----

### Initiate an order using Checkout Page

To init a pay request, we make a request object
using [StandardCheckoutPayRequest.builder()](#standard-checkout-pay-request-builder)

You will get to initiate the order using the `pay` function: [PAY](#standard-checkout-pay)

###### Code:

```java
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String redirectUrl = "https://www.merchant.com/redirect";

StandardCheckoutPayRequest standardCheckoutPayRequest = StandardCheckoutPayRequest.builder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .redirectUrl(redirectUrl)
        .build();

StandardCheckoutPayResponse standardCheckoutPayResponse = standardCheckoutClient.pay(standardCheckoutPayRequest);
String checkoutPageUrl = standardCheckoutPayResponse.getRedirectUrl();
```

The data will be in a [StandardCheckoutPayResponse](#standardcheckoutpayresponse-properties) object.
<br>The `checkoutPageUrl` you get can be handled by redirecting the user to that url on the front end.
____

### Check Status of an order

View the state for the order we just initiated. [checkOrderStatus](#order-status)

```java
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;

String merchantOrderId = "<merchantOrderId>";  //created at the time of order creation
OrderStatusResponse orderStatusResponse = standardCheckoutClient.getOrderStatus(merchantOrderId);
String state = orderStatusResponse.getState();
```

You will get the data [OrderStatusResponse](#orderstatusresponse-properties) Object
____

### Order Callback Handling

You will receive a callback which you have configured.
<br>It is important to check the validity of the callback received from PhonePe using the `validateCallback()` function.

```java
String username = "<username>";
String password = "<password>";

String authorization = "<authorization";
String responseBody = "<responseBody>";

CallbackResponse callbackResponse = standardCheckoutClient.validateCallback(username, password, authorization,
        responseBody);
String orderId = callbackResponse.getPayload()
        .getOrderId();
String state = callbackResponse.getPayload()
        .getState();
```

`validateCallback` will throw PhonePeException, if the callback is invalid.
<br>Possible refund callback states:<br> CHECKOUT_ORDER_COMPLETED,CHECKOUT_ORDER_FAILED,
CHECKOUT_TRANSACTION_ATTEMPT_FAILED details: [CallbackType](#callback-types)

_____

## Create Order SDK Integration

The `createSdkOrder()` function is used to create an order

```java
import java.util.UUID;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 1000;
String redirectUrl = "https://redirectUrl.com";

CreateSdkOrderRequest createSdkOrderRequest = CreateSdkOrderRequest.StandardCheckoutBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .redirectUrl(redirectUrl)
        .build();
CreateSdkOrderResponse createSdkOrderResponse = standardCheckoutClient.createSdkOrder(createSdkOrderRequest);
String token = createSdkOrderResponse.getToken();
```

The function returns a [CreateSdkOrderResponse](#createsdkorderresponse-properties) object. Merchant should
retrieve the token from the received response.
-----

# PhonePe PG JAVA SDK

## Class Initialization

StandardCheckoutClient class will be used to communicate with the PhonePe APIs. You can initiate the instance of this
class only once.

Use required credentials while initializing the object.

Disclaimer: For production builds don't save credentials in code.

### Parameters

 Parameter      | Type      | Mandatory | Description                                                                                         |
|----------------|-----------|-----------|-----------------------------------------------------------------------------------------------------|
| `clientId`     | `String`  | Yes       | Client ID for secure communication with PhonePe.                                                    |
| `clientSecret` | `String`  | Yes       | Secret provided by PhonePe. To be kept secure on the merchant side                                  |
| `clientVersion` | `Integer` | Yes       | Client version for secure communication with PhonePe.
| `env`          | `Env`     | Yes       | Environment for the StandardCheckoutClient: `Env.PRODUCTION` (production), `Env.SANDBOX` (testing). |

### Throws PhonePeException:

If another StandardCheckoutClient object is initialized, `PhonePeException` is thrown.

### Example usage:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;   //insert your client version here
Env env = Env.SANDBOX;       //change to Env.PRODUCTION when you go live

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

```

-----

## Standard Checkout Pay

This method is used to initiate a payment via the PhonePe PG

### Parameters

| Parameter                    | Type                         | Mandatory | Description                                                |
|------------------------------|------------------------------|-----------|------------------------------------------------------------|
| `standardCheckoutPayRequest` | `StandardCheckoutPayRequest` | Yes       | The request build using StandardCheckoutPayRequest Builder |

### Standard Checkout Pay Request Builder

Builds Pay Page Request `StandardCheckoutPayRequest.builder()`

### Attributes

| Parameter         | Type     | Mandatory | Description                                                     | Constraints                                                                                                           |
|-------------------|----------|-----------|-----------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| `merchantOrderId` | `String` | Yes       | The unique order ID assigned by the merchant                    | 1. Length should be less than 63 characters<br/>2. No special characters allowed except underscore “_” and hyphen “-“ |
| `amount`          | `long`   | Yes       | Order amount in Paisa                                           | 1. Minimum amount should be 1 Paisa                                                                                   |
| `redirectUrl`     | `String` | No        | URL where user will be redirected after success/failed payment. | -                                                                                                                     |

### Example Usage:

```java
import java.util.UUID;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String redirectUrl = "https://redirectUrl.com";

StandardCheckoutPayRequest standardCheckoutPayRequest = StandardCheckoutPayRequest.builder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .redirectUrl(redirecturl)
        .build();

StandardCheckoutPayResponse standardCheckoutPayResponse = standardCheckoutClient.pay(standardCheckoutPayRequest);
String redirectUrlForUI = standardCheckoutPayResponse.getRedirectUrl();
```

### Returns:

The function returns a `StandardCheckoutPayResponse` object with the following properties:

###### StandardCheckoutPayResponse Properties:

Here is the response property table for the given model:

| Property      | Type     | Description                                                                                      |
|---------------|----------|--------------------------------------------------------------------------------------------------|
| `state`       | `String` | State of the order. Expected value is PENDING.                                                   |
| `redirectUrl` | `String` | The url for the PG Standard Checkout (merchant is supposed to redirect user to complete payment) |
| `orderId`     | `String` | Order Id created by PhonePe                                                                      |
| `expireAt`    | `long`   | Order expire date in epoch                                                                       |

-----

## Order Status

Checks the status of an order.

### Parameters:

| Parameter       | Type     | Mandatory | Description                                                                                                                                 |
|-----------------|----------|-----------|---------------------------------------------------------------------------------------------------------------------------------------------|
| merchantOrderId | `String` | Yes       | The merchant order ID for which the status is fetched.                                                                                      |
| details         | `String` | No        | 1. True → return all attempt details under paymentDetails list <br/>2. False → return only latest attempt details under paymentDetails list |

### Example usage:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live
StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<merchantOrderId>";

OrderStatusResponse orderStatusResponse = standardCheckoutClient.getOrderStatus(merchantOrderId);
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

###### PaymentDetail Properties:

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

### Parameters

| Parameter       | Type     | Mandatory | Description                                       |
|-----------------|----------|-----------|---------------------------------------------------|
| `transactionId` | `String` | Yes       | The transaction attempt id received from PhonePe. |

### Example Usage:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<merchantOrderId>";

OrderStatusResponse transactionStatusResponse = standardCheckoutClient.getTransactionStatus(transactionId);
String state = transactionStatusResponse.getState();
```

### Returns

The function returns a [OrderStatusResponse](#orderstatusresponse-properties) mentioned in `getOrderStatus()` Function.

---

## Callback Verification

This is used to verify whether the callback received is valid or not

You need to pass 4 parameters to the `validateCallback()` function

### Parameters

| Parameter       | Type     | Mandatory | Description                                                     |
|-----------------|----------|-----------|-----------------------------------------------------------------|
| `username`      | `String` | Yes       | Unique username configured for the callback url                 |
| `password`      | `String` | Yes       | Unique password configured for the callback url                 |
| `authorization` | `String` | Yes       | Value of the `Authorization` header under the callback response |
| `responseBody`  | `String` | Yes       | Callback response body as string.                               |

### Example usage

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.CallbackResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live
StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String username = "<username>";
String password = "<password>";
String authorization = "<authorization>";
String responseBody = "<responseBody>";

CallbackResponse callbackResponse = standardCheckoutClient.validateCallback(username, password, authorization,
        responseBody);
String callbackType = callbackResponse.getType();
String merchantRefundId = callbackResponse.getPayload()
        .getMerchantRefundId();
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

###### Callback Types

| Callback Type                       | Context                                 |
|-------------------------------------|-----------------------------------------|
| CHECKOUT_ORDER_COMPLETED            | Order Completed for checkout            |
| CHECKOUT_ORDER_FAILED               | Order Failed                            |
| CHECKOUT_TRANSACTION_ATTEMPT_FAILED | Transaction Attempt Failed for checkout |
| PG_ORDER_COMPLETED                  | Order Completed for PG                  |
| PG_ORDER_FAILED                     | Order Failed for PG                     |
| PG_TRANSACTION_ATTEMPT_FAILED       | Transaction Attempt Failed for PG       |

###### CallbackData Properties

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
| `errorCode`         | `String`                                    | Error code present only when the transaction state is Failed                                                                                              |
| `detailedErrorCode` | `String`                                    | Detailed Error Code present only when transaction state is Failed                                                                                         |
| `rail`              | [PaymentRail](#paymentrail)                 | Contains processing rail details under which transaction attempt is made.                                                                                 |
| `instrument`        | [PaymentInstrumentV2](#paymentinstrumentv2) | Contains instrument details of that particular transaction Id                                                                                             |

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

### Example Usage:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.response.RefundResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantRefundId = UUID.randomUUID()
        .toString();
String originalMerchantOrderId = "<merchantOrderId>";  //orderId for which refund should be initiated
long amount = 100;

RefundRequest refundRequest = RefundRequest.builder()
        .merchantRefundId(merchantRefundId)
        .originalMerchantOrderId(merchantOrderId)
        .amount(amount)
        .build();

RefundResponse refundResponse = standardCheckoutClient.refund(refundRequest);
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
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.common.models.response.RefundStatusResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String refundId = "<refundId>";    //refundId used at the time of initiating refund

RefundStatusResponse refundStatusResponse = standardCheckoutClient.getRefundStatus(refundId);
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

###### PaymentRefundDetail

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
| `splitInstruments`  | List<[InstrumentCombo](#instrumentcombo)>   | Type of transaction instrument. It can be any one of the following types:<br/>1. ACCOUNT<br/>2. CREDIT_CARD<br/>3. DEBIT_CARD<br/>4. NET_BANKING          |

-----

## SDK Order Creation

`createSdkOrder()` is used to fetch the token required for UI to init pay requests.

### Parameters

| Parameter               | Type                    | Mandatory | Description                                                                      |
|-------------------------|-------------------------|-----------|----------------------------------------------------------------------------------|
| `createSdkOrderRequest` | `CreateSdkOrderRequest` | Yes       | The request is built using [SdkOrderRequest](#sdk-order-request-builder) Builder |

### Sdk Order Request Builder

Builds SDK order request

| Parameter         | Type     | Mandatory | Description                                                                   |
|-------------------|----------|-----------|-------------------------------------------------------------------------------|
| `merchantOrderId` | `String` | Yes       | Unique order ID generated by merchant                                         |
| `amount`          | `long`   | Yes       | Amount of order in Paisa                                                      |
| `redirectUrl`     | `String` | Yes       | The URL to which the user should be redirected after the payment is completed |

### Example Usage:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.CreateSdkOrderRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.CreateSdkOrderResponse;
import java.util.UUID;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live
StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);


String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String redirectUrl = "https://www.merchant.com/redirect";

CreateSdkOrderRequest createSdkOrderRequest = CreateSdkOrderRequest.StandardCheckoutBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .redirectUrl(redirectUrl)
        .build();

CreateSdkOrderResponse createSdkOrderResponse = standardCheckoutClient.createSdkOrder(createSdkOrderRequest);
String token = createSdkOrderResponse.getToken();
```

### Returns

The function return a [CreateSdkOrderResponse](#createsdkorderresponse-properties) properties:

### CreateSdkOrderResponse Properties

| Property   | Type     | Description                                      |
|------------|----------|--------------------------------------------------|
| `orderId`  | `String` | Order ID generated by PhonePE                    |
| `state`    | `String` | State of the Order. Initially it will be PENDING |
| `expireAt` | `long`   | Expiry time in epoch                             |
| `token`    | `String` | Token used to access the PG Page.                |

-----

## Exception Handling

`PhonePeException` is raised by the PhonePe API's.

### Attributes

| Attribute        | Type                 | Description                                                       |
|------------------|----------------------|-------------------------------------------------------------------|
| `httpStatusCode` | `Integer`            | The status code of the http response.                             |
| `message`        | `String`             | The http error message.                                           |
| `data`           | `Map<String,String>` | The details of the error that happened while calling PhonePe API. |
| `code`           | `String`             | Code sent by the PhonePe why it occurred                          |

### Example usage

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;      //insert your client version here
Env env = Env.SANDBOX;          //change to Env.PRODUCTION when you go live

StandardCheckoutClient standardCheckoutClient = StandardCheckoutClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<duplicateId>";  //will throw exception
long amount = 100;
String redirectUrl = "https://merchant.com/redirectUrl";

StandardCheckoutPayRequest standardCheckoutPayRequest = StandardCheckoutPayRequest.StandardCheckoutRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .amount(amount)
        .redirectUrl(redirectUrl)
        .build();
try{
    StandardCheckoutPayResponse standardCheckoutPayResponse = standardCheckoutClient.pay(standardCheckoutPayRequest);
} catch(PhonePeException phonePeException){
Integer httpStatusCode = phonePeException.getHttpStatusCode();
String message = phonePeException.getMessage();
Map<String, String> data = phonePeException.getData();
String code = phonePeException.getCode();
}
```

-----

## Response Models for Reference

### PaymentRail

Different types of rail which will be received at the time of `getOrderStatus()`. It
falls under the `rail` attribute in [PaymentDetail](#paymentdetail-properties) Object

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

Different types of instruments which will be received at the time of `getOrderStatus()`. It
falls under the `instrument` attribute in [PaymentDetail](#paymentdetail-properties) Object

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

