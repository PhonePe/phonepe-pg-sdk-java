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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisher;
import com.phonepe.sdk.pg.common.events.publisher.EventPublisherFactory;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SingletonEventPublisherTest {

    @Test
    void testEventPublisherWithDifferentClients() {

        EventPublisherFactory eventPublisherFactory1 =
                new EventPublisherFactory(new ObjectMapper(), new OkHttpClient(), "hostUrl");
        EventPublisherFactory eventPublisherFactory2 =
                new EventPublisherFactory(new ObjectMapper(), new OkHttpClient(), "hostUrl");

        EventPublisher eventPublisher1 = eventPublisherFactory1.getEventPublisher(true);
        EventPublisher eventPublisher2 = eventPublisherFactory2.getEventPublisher(true);

        Assertions.assertEquals(eventPublisher1, eventPublisher2);
    }
}
