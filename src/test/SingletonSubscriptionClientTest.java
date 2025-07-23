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
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.subscription.v2.SubscriptionClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingletonSubscriptionClientTest extends BaseSetup {

    @Test
    void testSingletonViaGetInstance() {
        SubscriptionClient subscriptionClient1 =
                SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, env);
        SubscriptionClient subscriptionClient2 =
                SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, env);

        Assertions.assertEquals(subscriptionClient1, subscriptionClient2);
    }

    @Test
    void testSingletonWithDiffParameters() {
        SubscriptionClient subscriptionClient1 =
                SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, env);
        SubscriptionClient subscriptionClient2 = SubscriptionClient.getInstance(
                "clientId2", "clientSecret2", 1, Env.TEST);
        Assertions.assertNotEquals(subscriptionClient1, subscriptionClient2);
        Assertions.assertNotNull(subscriptionClient2);
    }

    @Test
    void testSingletonWithDifferentEnvironments() {
        SubscriptionClient subscriptionClientProd =
                SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, Env.PRODUCTION);
        SubscriptionClient subscriptionClientSandbox =
                SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, Env.SANDBOX);

        Assertions.assertNotEquals(subscriptionClientProd, subscriptionClientSandbox);

        SubscriptionClient subscriptionClientProd2 =
                SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, Env.PRODUCTION);
        Assertions.assertEquals(subscriptionClientProd, subscriptionClientProd2);

        SubscriptionClient subscriptionClientSandbox2 =
                SubscriptionClient.getInstance(clientId, clientSecret, clientVersion, Env.SANDBOX);
        Assertions.assertEquals(subscriptionClientSandbox, subscriptionClientSandbox2);
    }
}
