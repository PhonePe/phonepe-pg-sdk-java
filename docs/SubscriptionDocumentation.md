# Subscription SDK

---

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

Create an instanceof the [SubscriptionClient](#class-initialization) class:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, env);
```

_____

## [Subscription Setup](#subscription-setup-request)

### 1. Setup Via UPI_INTENT

To setup the subscription via UPI_INTENT, merchant need to
use [PgPaymentRequest.SubscriptionSetupUpiIntentBuilder()](#1-using-upi_intent)

### Code:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import java.util.UUID;

String merchantOrderId = UUID.randomUUID()
        .toString();
String merchantSubscriptionId = UUID.randomUUID()
        .toString();
long amount = 200;                                                  //In paisa
AuthWorkflowType authWorkFlowType = AuthWorkflowType.TRANSACTION;   //It can also be AuthWorkFlowType.PENNY_DROP
AmountType amountType = AmountType.FIXED;                           //It can also be AmountType.VARIBALE
Frequency frequency = Frequency.ON_DEMAND;
long maxAmount = 200;                                               //In paisa

PgPaymentRequest setupRequest = PgPaymentRequest.SubscriptionSetupUpiIntentBuilder()
        .merchantOrderId(merchantOrderId)
        .merchantSubscriptionId(merchantSubscriptionId)
        .amount(amount)
        .authWorkflowType(authWorkFlowType)
        .amountType(amountType)
        .maxAmount(maxAmount)
        .frequency(frequency)
        .build();

PgPaymentResponse response = client.setup(setupRequest);
String state = response.state;
```

The response will be [PgPaymentResponse](#response-properties-pgpaymentresponse-upi_intent)

### 2. Setup via UPI_COLLECT

To setup the subscription via UPI_COLLECT, merchant need to
use [PgPaymentRequest.SubscriptionSetupUpiCollectBuilder()](#2-using-upi_collect)

###### Code:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import java.util.UUID;

String merchantOrderId = UUID.randomUUID()
        .toString();
String merchantSubscriptionId = UUID.randomUUID()
        .toString();
long amount = 200;                                                  //In paisa
String vpa = "9999999999@ibl";
AuthWorkflowType authWorkFlowType = AuthWorkflowType.TRANSACTION;   //It can also be AuthWorkFlowType.PENNY_DROP
AmountType amountType = AmountType.FIXED;                           //It can also be AmountType.VARIBALE
Frequency frequency = Frequency.ON_DEMAND;
long maxAmount = 200;                                               //In paisa

PgPaymentRequest setupRequest = PgPaymentRequest.SubscriptionSetupUpiCollectBuilder()
        .merchantOrderId(merchantOrderId)
        .merchantSubscriptionId(merchantSubscriptionId)
        .amount(amount)
        .vpa(vpa)
        .authWorkflowType(authWorkFlowType)
        .amountType(amountType)
        .maxAmount(maxAmount)
        .frequency(frequency)
        .build();

PgPaymentResponse setupResponse = client.setup(setupRequest);
String state = setupResponse.getState();
```

The response will be in a [PgPaymentResponse](#response-properties-pgpaymentresponse-upi_collect) object.

Merchant can extract the state from the response
____

### [Notify Request](#subscription-notify)

Send a notify request for the subscription ID used at the time of
setup. [PgPaymentRequest.SubscriptionNotifyRequestBuilder()](#subscription-notify)

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import java.util.UUID;

String merchantOrderId = UUID.randomUUID()
        .toString();      //MERCHANT_ORDER_ID will be used in the redeem call
String merchantSubscriptionId = "<MERCHANT_SUBSCRIPTION_ID>";
RedemptionRetryStrategy redemptionRetryStrategy = RedemptionRetryStrategy.STANDARD;
boolean autoDebit = false;
long amount = 200;                                      //In paisa

PgPaymentRequest subscriptionRequest = PgPaymentRequest.SubscriptionNotifyRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .merchantSubscriptionId(merchantSubscriptionId)
        .autoDebit(autoDebit)
        .redemptionRetryStrategy(redemptionRetryStrategy)
        .amount(200)
        .build();

PgPaymentResponse notifyResponse = client.notify(request);
String state = notifyResponse.getState();
```

You will get the response as [PgPaymentResponse](#response-properties-pgpaymentresponse-notify) Object
____

### [Redeem Request](#subscription-redeem-request)

Merchant can call the `redeem()` to start the subscription using same `merchantOrderId` used in the notify call

```java
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionRedeemResponseV2;

String merchantOrderId = "<MERCHANT_ORDER_ID>";         //Used at the time of making the notify request

SubscriptionRedeemResponseV2 response = client.redeem(merchantOrderId);
String state = response.getState();
```

The response received will be a [SubscriptionRedeemResponseV2](#response-properties-subscriptionredeemresponsev2);

_____

### [Subscription Cancellation](#subscription-cancellation-request)

Merchant can cancel/stop the subscription by using the `cancelSubsctiption()`

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String merchantSubscriptionId = "<MERCHANT_SUBSCRIPTION_ID>";       //MERCHANT_SUBSCRIPTION_ID used for setup and notify


client.cancelSubscription(merchantSubscriptionId);
```

The response is `void` and it will stop/cancel the subscription for given ID

------

### [Subscription Status](#subscription-status-request)

Gets the status of the subscription by providing the `MERCHANT_SUBSCRIPTION_ID`

```java
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionStatusResponseV2;

String merchantSubscriptionId = "<MERCHANT_SUBSCRIPTION_ID>";

SubscriptionStatusResponseV2 statusResponse = client.getSubscriptionStatus(merchantSubscriptionId);
String state = statusResponse.getState();
```

The response received will be [SubscriptionStatusResponseV2](#response-properties-subscriptionstatusresponsev2)

-----

### [Order Status](#order-status-request)

Gets the status of the order by providing the `MERCHANT_ORDER_ID`

```java
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;

String merchantOrderId = "<MERCHANT_ORDER_ID>";

OrderStatusResponse statusResponse = client.getOrderStatus(merchantOrderId);
String state = statusResponse.getState();
```

The response received will be [OrderStatusResponse](#response-properties-orderstatusresponse)

----

### [Transaction Status](#transaction-status-request)

Gets the status for a particular transactionID

```java
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;

String transactionId = "<TRANSACTION_ID>";

OrderStatusResponse statusResponse = client.getTransactionStatus(transactionId);
String state = statusResponse.getState();
```

The response received will be [OrderStatusResponse](#response-properties-orderstatusresponse)

------

## PhonePe PG JAVA SDK:

### [Class Initialization](#quick-start)

`SubscriptionClient` class will be used to communicate with the PhonePe APIs. You can initiate the instance of this
class only once.
Use required credentials while initializing the object.

Disclaimer: For production builds don't save credentials in code.

#### Parameters:

| Parameter      | Type      | Mandatory | Description                                                                                              |
|----------------|-----------|-----------|----------------------------------------------------------------------------------------------------------|
| `clientId`     | `String`  | Yes       | Unique Client ID provided by PhonePe.                                                                    |
| `clientSecret` | `String`  | Yes       | Unique Client Secret provided by PhonePe.                                                                |
| `cientVersion` | `Integer` | Yes       | Unique Client Version provided by PhonePe.                                                               |
| `env`          | `Env`     | Yes       | Environment for the SubscriptionClient: <br/>`Env.PRODUCTION` (production) <br/>`Env.SANDBOX` (testing). |

#### Throws PhonePeException:

If another `SubscriptionClient` object is initialized, `PhonePeException` is thrown.

#### Example:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;   //insert your client version here
Env env = Env.SANDBOX;       //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);
```

-----

### [Subscription Setup Request](#subscription-setup)

The `setup()` method is used to start/create a new subscription using any one of the given instruments:

1. UPI_INTENT
2. UPI_COLLECT

### [1. Using UPI_INTENT]()

Uses `PgPaymentRequest.SubscriptionSetupUpiIntentBuilder()`

### Parameters:

| Parameter                | Type               | Mandatory | Description                                                                                                                                                           | Default Value |
|--------------------------|--------------------|-----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| `merchantOrderId`        | `String`           | Yes       | Unique order ID generated by merchant                                                                                                                                 |
| `merchantSubscriptionId` | `String`           | Yes       | Unique subscription ID generated by merchant                                                                                                                          |
| `amount`                 | `long`             | Yes       | Amount of order in Paisa<br/>1. FULL auth - first debit amount<br/>2. PENNY auth - 200                                                                                |
| `orderExpireAt`          | `long`             | No        | Order expireAt epoch after which order will auto fail if not terminal                                                                                                 | 10 mins       |
| `authWorkflowType`       | `AuthWorkflowType` | Yes       | Type of setup workflow    <br/>1. TRANSACTION<br/>2. PENNY_DROP                                                                                                       |
| `amountType`             | `AmoountType`      | Yes       | Nature of redemption amount<br/>1. FIXED<br/>2. VARIABLE                                                                                                              |
| `maxAmount`              | `long`             | Yes       | Max amount upto which redemptions will be allowed                                                                                                                     |
| `frequency`              | `Frequency`        | Yes       | Subscription frequency<br/>1. DAILY<br/>2. WEEKLY<br/>3. MONTHLY<br/>4. YEARLY<br/>5. FORTNIGHTLY<br/>6. BIMONTHLY<br/>7. ON_DEMAND<br/>8. QUATERLY<br/>9. HALFYEARLY |
| `subscriptionExpireAt`   | `long`             | No        | Subscription cycle expiry. No operation allowed after subscription expires                                                                                            | 30 years
| `targetApp`              | `String`           | No        | Target app for intent payment mode<br/>1. android - package name<br/>2. iOS - PHONEPE / GPAY / PAYTM                                                                  |
| `metaInfo`               | `MetaInfo`         | No        | User defines fields propagated in status check & calbacks                                                                                                             |

### Example:

```java
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import java.util.UUID;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;                      //insert your client version here
Env env = Env.SANDBOX;                          //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
String merchantSubscriptionId = UUID.randomUUID()
        .toString();
long amount = 200;                                                  //In paisa
AuthWorkflowType authWorkFlowType = AuthWorkflowType.TRANSACTION;   //It can also be AuthWorkFlowType.PENNY_DROP
AmountType amountType = AmountType.FIXED;                           //It can also be AmountType.VARIBALE
Frequency frequency = Frequency.ON_DEMAND;
long maxAmount = 200;

PgPaymentRequest setupRequest = PgPaymentRequest.SubscriptionSetupUpiIntentBuilder()
        .merchantOrderId(merchantOrderId)
        .merchantSubscriptionId(merchantSubscriptionId)
        .amount(amount)
        .authWorkflowType(authWorkFlowType)
        .amountType(amountType)
        .maxAmount(maxAmount)
        .frequency(frequency)
        .build();


PgPaymentResponse setupResponse = subscriptionClient.setup(setupRequest);
String intentUrl = setupResponse.getIntentUrl();
```

Extract the `intentUrl` from the response received

### Response Properties: PgPaymentResponse (UPI_INTENT)

| Property    | Type     | Description                                                     |
|-------------|----------|-----------------------------------------------------------------|
| `orderId`   | `String` | Unique order ID generated by PhonePe                            |
| `state`     | `String` | State of the order initiated. Initially it will be `PENDING`    |
| `expireAt`  | `long`   | Expire time in epoch	                                           |
| `intentUrl` | `String` | Intent url according to the targetApp mentioned in the request	 |

### [2. Using UPI_COLLECT]()

Uses `PgPaymentRequest.SubscriptionSetupUpiCollectBuilder()`

### Parameters:

| Parameter                | Type               | Mandatory | Description                                                                                                                                                           | Default Value |
|--------------------------|--------------------|-----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| `merchantOrderId`        | `String`           | Yes       | Unique order ID generated by merchant                                                                                                                                 |
| `merchantSubscriptionId` | `String`           | Yes       | Unique subscription ID generated by merchant                                                                                                                          |
| `amount`                 | `long`             | Yes       | Amount of order in Paisa<br/>1. FULL auth - first debit amount<br/>2. PENNY auth - 200                                                                                |
| `orderExpireAt`          | `long`             | No        | Order expireAt epoch after which order will auto fail if not terminal                                                                                                 | 10 mins       |
| `authWorkflowType`       | `AuthWorkflowType` | Yes       | Type of setup workflow    <br/>1. TRANSACTION<br/>2. PENNY_DROP                                                                                                       |
| `amountType`             | `AmoountType`      | Yes       | Nature of redemption amount<br/>1. FIXED<br/>2. VARIABLE                                                                                                              |
| `maxAmount`              | `long`             | Yes       | Max amount upto which redemptions will be allowed                                                                                                                     |
| `frequency`              | `Frequency`        | Yes       | Subscription frequency<br/>1. DAILY<br/>2. WEEKLY<br/>3. MONTHLY<br/>4. YEARLY<br/>5. FORTNIGHTLY<br/>6. BIMONTHLY<br/>7. ON_DEMAND<br/>8. QUATERLY<br/>9. HALFYEARLY |
| `subscriptionExpireAt`   | `long`             | No        | Subscription cycle expiry. No operation allowed after subscription expires                                                                                            | 30 years
| `metaInfo`               | `MetaInfo`         | No        | User defines fields propagated in status check & calbacks                                                                                                             |
| `vpa`                    | `String`           | Yes       | Vpa for which collect request will be raised                                                                                                                          |

### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;
import com.phonepe.sdk.pg.subscription.v2.models.request.AmountType;
import com.phonepe.sdk.pg.subscription.v2.models.request.AuthWorkflowType;
import com.phonepe.sdk.pg.subscription.v2.models.request.Frequency;
import java.util.UUID;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;                      //insert your client version here
Env env = Env.SANDBOX;                          //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
String merchantSubscriptionId = UUID.randomUUID()
        .toString();
long amount = 200;                                                  //In paisa
AuthWorkflowType authWorkFlowType = AuthWorkflowType.TRANSACTION;   //It can also be AuthWorkFlowType.PENNY_DROP
AmountType amountType = AmountType.FIXED;                           //It can also be AmountType.VARIBALE
Frequency frequency = Frequency.ON_DEMAND;
String vpa = "VALID_VPA";
long maxAmount = 200;

PgPaymentRequest setupRequest = PgPaymentRequest.SubscriptionSetupUpiCollectBuilder()
        .merchantOrderId(merchantOrderId)
        .merchantSubscriptionId(merchantSubscriptionId)
        .amount(amount)
        .authWorkflowType(authWorkFlowType)
        .amountType(amountType)
        .maxAmount(maxAmount)
        .frequency(frequency)
        .vpa(vpa)
        .build();

PgPaymentResponse setupResponse = subscriptionClient.setup(setupRequest);
```

It will raise a collect request to the mentioned vpa

### Response Properties: PgPaymentResponse (UPI_COLLECT)

| Property   | Type     | Description                                                  |
|------------|----------|--------------------------------------------------------------|
| `orderId`  | `String` | Unique order ID generated by PhonePe                         |
| `state`    | `String` | State of the order initiated. Initially it will be `PENDING` |
| `expireAt` | `long`   | Expire time in epoch	                                        |

-----

### [Subscription Notify]()

The `notify()` method is used to send the notification to the subscribed users.

Uses `PgPaymentRequest.SubscriptionNotifyRequestBuilder()`

### Parameters:

| Parameter                 | Type                      | Mandatory | Description                                                                                                                | Default Value |
|---------------------------|---------------------------|-----------|----------------------------------------------------------------------------------------------------------------------------|---------------|
| `merchantOrderId`         | `String`                  | Yes       | Unique order ID generated by merchant                                                                                      |
| `merchantSubscriptionId`  | `String`                  | Yes       | Unique subscription ID generated by merchant                                                                               |
| `amount`                  | `long`                    | Yes       | Amount of order in Paisa<br/>1. FULL auth - first debit amount<br/>2. PENNY auth - 200                                     |
| `orderExpireAt`           | `long`                    | No        | Order expireAt epoch after which order will auto fail if not terminal                                                      | 48 hours      |
| `redemptionRetryStrategy` | `RedemptionRetryStrategy` | No        | Redemption retry strategy in case attempts fail<br/>1. STANDARD - Internal Retries<br/>2. CUSTOM - Merchant needs to retry | STANDARD      |
| `autoDebit`               | `MetaInfo`                | No        | Auto debit redemption 24 hours after notify success          <br/>Can't be true for CUSTOM redemptionRetryStrategy         | false
| `metaInfo`                | `MetaInfo`                | No        | User defines fields propagated in status check & calbacks                                                                  | null

### Example:

```java
import com.phonepe.sdk.pg.common.models.request.PgPaymentRequest;
import com.phonepe.sdk.pg.common.models.response.PgPaymentResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;
import com.phonepe.sdk.pg.subscription.v2.models.request.RedemptionRetryStrategy;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;                      //insert your client version here
Env env = Env.SANDBOX;                          //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = UUID.randomUUID()
        .toString();
String merchantSubscriptionId = "<MERCHANT_SUBSCRIPTION_ID>";       //Use same subscription ID created at the time of setup
RedemptionRetryStrategy redemptionRetryStrategy = RedemptionRetryStrategy.STANDARD;
boolean autoDebit = false;
long amount = 100;

PgPaymentRequest notifyRequest = PgPaymentRequest.SubscriptionNotifyRequestBuilder()
        .merchantOrderId(merchantOrderId)
        .merchantSubscriptionId(merchantSubscriptionId)
        .autoDebit(autoDebit)
        .redemptionRetryStrategy(redemptionRetryStrategy)
        .amount(amount)
        .build();


PgPaymentResponse notifyResponse = subscriptionClient.notify(notifyRequest);
String state = notifyResponse.getState();           //state will be NOTIFICATION_IN_PROGRESS
```

### Response properties: PgPaymentResponse (Notify)

| Property   | Type     | Description                                                        |
|------------|----------|--------------------------------------------------------------------|
| `orderId`  | `String` | Unique order ID generated by PhonePe                               |
| `state`    | `String` | State of the order notified. It will be `NOTIFICATION_IN_PROGRESS` |
| `expireAt` | `long`   | Expire time in epoch	                                              |

-----

### [Subscription Redeem Request](#redeem-request)

The `redeem()` call is used to initiate the redemption which is after 24 hours of notify() method called

Parameters

| Parameter         | Type     | Mandatory | Description                                                  |
|-------------------|----------|-----------|--------------------------------------------------------------|
| `merchantOrderId` | `String` | Yes       | Same merchantOrderId passed in corresponding notify request	 |

### Example:

```java
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionRedeemResponseV2;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;                      //insert your client version here
Env env = Env.SANDBOX;                          //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<MERCHANT_ORDER_ID>";     //Used at the time of notify()

SubscriptionRedeemResponseV2 subscriptionRedeemResponseV2 = subscriptionClient.redeem(merchantOrderId);
```

### Response properties: SubscriptionRedeemResponseV2

| Property        | Type     | Description                  |
|-----------------|----------|------------------------------|
| `state`         | `String` | State of the order           |
| `transactionId` | `long`   | ID generated by PhonePe side |

---

### [Subscription Cancellation Request](#subscription-cancellation)

The `cancelSubscription()` method is used to stop/cancel the subscription

### Parameters:

| Parameter              | Type     | Mandatory | Description                  |
|------------------------|----------|-----------|------------------------------|
| merchantSubscriptionId | `String` | Yes       | Id generated by the merchant |

### Example:

```java
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

String merchantSubscriptionId = "<MERCHANT_SUBSCRIPTION_ID>";

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, env);
subscriptionClient.cancelSubscripition(merchantSubscriptionId);
```

The function type is `void`, ie it does not return anything

---

### [Subscription Status Request](#subscription-status)

### Parameters:

| Parameter              | Type     | Mandatory | Description                  |
|------------------------|----------|-----------|------------------------------|
| merchantSubscriptionId | `String` | Yes       | Id generated by the merchant |

### Example:

```java
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;
import com.phonepe.sdk.pg.subscription.v2.models.response.SubscriptionStatusResponseV2;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

String merchantSubscriptionId = "<MERCHANT_SUBSCRIPTION_ID>";

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, env);
SubscriptionStatusResponseV2 status = subscriptionClient.getSubscriptionStatus(merchantSubscriptionId);
```

### Response Properties: [SubscriptionStatusResponseV2]()

| Property                 | Type               | Description                                                                                                                                                           |
|--------------------------|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `merchantSubscriptionId` | `String`           | Unique order ID generated by PhonePe                                                                                                                                  |
| `subscriptionId`         | `String`           | Unique Subscription Id generated by PhonePe                                                                                                                           |
| `state`                  | `String`           | State of the subscription<br/>1. ACTIVE<br/>2. CANCELLED<br/>3. REVOKED                                                                                               |
| `authWorkflowType`       | `AuthWorkflowType` | Type of setup workflow    <br/>1. TRANSACTION<br/>2. PENNY_DROP                                                                                                       |
| `amountType`             | `AmoountType`      | Nature of redemption amount<br/>1. FIXED<br/>2. VARIABLE                                                                                                              |
| `maxAmount`              | `long`             | Max amount upto which redemptions will be allowed                                                                                                                     |
| `frequency`              | `Frequency`        | Subscription frequency<br/>1. DAILY<br/>2. WEEKLY<br/>3. MONTHLY<br/>4. YEARLY<br/>5. FORTNIGHTLY<br/>6. BIMONTHLY<br/>7. ON_DEMAND<br/>8. QUATERLY<br/>9. HALFYEARLY |
| `expireAt`               | `long`             | Subscription cycle expiry. No operation allowed after subscription expires                                                                                            |
| `pauseStartData`         | `long`             | If state is paused, then the field will be present                                                                                                                    | Vpa for which collect request will be raised                                                                                                                          |
| `pauseEndDate`           | `long`             | If state is paused, then the field will be present                                                                                                                    | Vpa for which collect request will be raised                                                                                                                                                                                                                                                                                             | Vpa for which collect request will be raised                                                                                                                          |

---

### [Order Status Request](#order-status)

It is used to retrieve the status of an order using `getOrderStatus()` function.

### Parameters:

| Parameter       | Type     | Mandatory | Description                                            |
|-----------------|----------|-----------|--------------------------------------------------------|
| merchantOrderId | `String` | Yes       | The merchant order ID for which the status is fetched. |

### Example:

```java
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String merchantOrderId = "<merchantOrderId>";

OrderStatusResponse orderStatusResponse = subscriptionClient.getOrderStatus(merchantOrderId);
String state = orderStatusResponse.getState();

```

### Response Properties: OrderStatusResponse

| Property         | Type                                             | Description                                                                                                         |
|------------------|--------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| `orderId`        | `String`                                         | Order ID created by PhonePe.                                                                                        |
| `state`          | `String`                                         | State of the order. It can be in any one of the following states: <br/> 1. PENDING <br/> 2. FAILED<br/>3. COMPLETED |
| `amount`         | `long`                                           | Order amount in Paise                                                                                               |
| `expireAt`       | `long`                                           | Order expiry time in epoch                                                                                          |
| `paymentDetails` | List<[PaymentDetail](#paymentdetail-properties)> | Contain list of details of each transaction attempt made corresponding to this particular order                     |

### PaymentDetail Properties:

| Property            | Type                                         | Description                                                                                                                                               |
|---------------------|----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `paymentMode`       | `String`                                     | Mode of Payment. It can be anyone of the following modes: <br/>1. UPI_INTENT<br/>2. UPI_COLLECT<br/>3. UPI_QR<br/>4. CARD<br/>5. TOKEN<br/>6. NET_BANKING |
| `timestamp`         | `long`                                       | Timestamp of the attempted transaction in epoch                                                                                                           |
| `amount`            | `long`                                       | Order amount in Paisa                                                                                                                                     |
| `transactionId`     | `String`                                     | Transaction Id generated by the PhonePe                                                                                                                   |
| `state`             | `String`                                     | Attempted transaction state. It can be any one of the following states: <br/>1. PENDING<br/>2. COMPLETED<br/>3. FAILED                                    |
| `errorCode`         | `String`                                     | Error code (Only present when transaction state is failed)                                                                                                |
| `detailedErrorCode` | `String`                                     | Detailed Error Code (Only present when transaction state is failed)                                                                                       |
| `paymentFlow`       | [PaymentFlowResponse](#paymentflow-response) | Shows the flow of the order status <br/>[1. SETUP](#1-subscription-setup)<br/>[2. REDEMPTION](#2-subscription-redemption)                                 |
| `rail`              | [PaymentRail](#paymentrail)                  | Contains processing rail details under which transaction attempt is made.                                                                                 |
| `instrument`        | [PaymentInstrumentV2](#paymentinstrumentv2)  | Contains instrument details of that particular transaction Id                                                                                             |
| `splitInstruments`  | List<[InstrumentCombo](#instrumentcombo)>    | Contains split instrument details of all the transactions made                                                                                            |

### PaymentFlow Response

### 1. Subscription Setup

| Property                 | Type               | Description                                                                 |
|--------------------------|--------------------|-----------------------------------------------------------------------------|
| `merchantSubscriptionId` | `String`           | Id generated by the merchant                                                |
| `authWorkflowType`       | `AuthWorkflowType` | Type of setup workflow                                                      |
| `amountType`             | `AmountType`       | Nature of redemption amount                                                 |
| `maxAmount`              | `long`             | Max amount upto which redemptions will be allowed                           |
| `frequency`              | `Frequency`        | Subscription frequency                                                      |
| `expireAt`               | `long`             | Subscription cycle expiry. No operation allowed after subscription expires. |
| `subscriptionId`         | `long`             | Id generated by the PhonePe side                                            |

### 2. Subscription Redemption

| Property                  | Type                      | Description                                                                                              |
|---------------------------|---------------------------|----------------------------------------------------------------------------------------------------------|
| `merchantSubscriptionId`  | `String`                  | Id generated by the merchant                                                                             |
| `redemptionRetryStrategy` | `RedemptionRetryStrategy` | Redemption retry strategy in case attempts fail                                                          |
| `autoDebit`               | `boolean`                 | Auto debit redemption 24 hours after notify success<br/>Can't be true for CUSTOM redemptionRetryStrategy |
| `validAfter`              | `long`                    | Time after which redeem can be called (epoch)                                                            |
| `validUpto`               | `long`                    | Time till redeem can be called (epoch)                                                                   |
| `notifiedAt`              | `long`                    | TIme at which notifed was called (epoch)                                                                 |

### InstrumentCombo

| Property     | Type                  | Description                                            |
|--------------|-----------------------|--------------------------------------------------------|
| `instrument` | `PaymentInstrumentV2` | Instrument used for the payment                        |
| `rails`      | `PaymentRail`         | Rail used for the payment                              |
| `amount`     | `long`                | Amount transferred using the above instrument and rail |

-----

### [Transaction Status Request](#transaction-status)

Checks the status of a transaction.

### Parameters:

| Parameter       | Type     | Mandatory | Description                                       |
|-----------------|----------|-----------|---------------------------------------------------|
| `transactionId` | `String` | Yes       | The transaction attempt id received from PhonePe. |

### Example

```java
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String transaction = "<TRANSACTION_ID>";

OrderStatusResponse transactionStatusResponse = subscriptionClient.getTransactionStatus(transactionId);
String state = transactionStatusResponse.getState();
```

### Returns

The function returns a [OrderStatusResponse](#response-properties-orderstatusresponse) mentioned in Check Status
Function.

---

### Refund Request

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
import com.phonepe.sdk.pg.common.models.request.RefundRequest;
import com.phonepe.sdk.pg.common.models.response.RefundResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
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

RefundResponse refundResponse = subscriptionClient.refund(refundRequest);
String state = refundResponse.getState();
```

### Response Properties: [RefundResponse]()

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
import com.phonepe.sdk.pg.common.models.response.RefundStatusResponse;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;

String clientId = "<clientId>";
String clientSecret = "<clientSecret>";
Integer clientVersion = 1;  //insert your client version here
Env env = Env.SANDBOX;      //change to Env.PRODUCTION when you go live

SubscriptionClient subscriptionClient = SubscriptionClient.getInstance(clientId, clientSecret,
        clientVersion, env);

String refundId = "<REFUND_ID>";

RefundStatusResponse refundStatusResponse = subscriptionClient.getRefundStatus(refundId);
String state = refundStatusResponse.getState();
```

### Response Properties: [RefundStatusResponse]()

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
















