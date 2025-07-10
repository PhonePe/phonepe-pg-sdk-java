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
import com.phonepe.sdk.pg.common.CommonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
    void testSha256SuccessFor5Parameters() {
        String clientId = "";
        String clientSecret = "";
        Integer clientVersion = 1;
        String expected = "364112ec2ed6825c04df3ba71d9276c7542b28972f491fc4eb5a8e8c1a73c717";
        Assertions.assertEquals(
                expected,
                CommonUtils.calculateSha256(
                        clientId, clientSecret, clientVersion, Env.TEST, false));
    }

    @Test
    void testSha256SuccessFor2Parameters() {
        String userId = "";
        String password = "";
        String expected = "e7ac0786668e0ff0f02b62bd04f45ff636fd82db63b1104601c975dc005f3a67";
        Assertions.assertEquals(expected, CommonUtils.calculateSha256(userId, password));
    }
}
