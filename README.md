# PhonePe B2B PG SDK

A java library for integrating with PhonePe API's

-----

## Installation

Requirements:

1) Java 8 or later

### Maven users

Add the dependency to your project's POM file:

```xml

<dependency>
    <groupId>com.phonepe</groupId>
    <artifactId>pg-sdk-java</artifactId>
    <version>2.1.3</version>
</dependency>
```

Add the PhonePe repository where the PhonePe SDK artifact is hosted to the distributionManagement:

```xml

<repositories>
    <repository>
        <id>io.cloudrepo</id>
        <name>PhonePe JAVA SDK</name>
        <url>https://phonepe.mycloudrepo.io/public/repositories/phonepe-pg-sdk-java</url>
    </repository>
</repositories>
```

### Gradle users

Add the following to your project's build.gradle file.
In the repositories section, add the URL for the PhonePe repository, and include the pg-sdk-java JAR in your
dependencies.

```java
repositories {
    maven {
        url "https://phonepe.mycloudrepo.io/public/repositories/phonepe-pg-sdk-java"
    }
}

dependencies {
    implementation 'com.phonepe:pg-sdk-java:2.1.3'
}
```

-----

## Quick start:

To get your keys, please visit the Merchant Onboarding of PhonePe
PG: [Merchant Onboarding](https://developer.phonepe.com/v1/docs/merchant-onboarding)

You will need three things to get started: `clientId`, `clientSecret` & `clientVersion`

Create an instanceof the [StandardCheckoutClient](#class-initialization) class:

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

_____

### Initiate an order using Checkout Page

To init a pay request, we make a request object
using [StandardCheckoutPayRequest.Buidler()](#standard-checkout-pay-request-builder)

You will get to initiate the order using the `pay` function: [PAY](#pay-function)

###### Code:

```java
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;

String merchantOrderId = UUID.randomUUID()
        .toString();
long amount = 100;
String redirectUrl = "https://www.merchant.com/redirect";

StandardCheckoutPayRequest standardCheckoutPayRequest = StandardCheckoutPayRequest.Buidler()
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

### Check Status of a order

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

You will receive a callback you have configured [dashboard link] 
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
CHECKOUT_TRANSACTION_ATTEMPT_FAILED details: [link to detail handling]

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

For more details, please visit https://developer.phonepe.com/

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
