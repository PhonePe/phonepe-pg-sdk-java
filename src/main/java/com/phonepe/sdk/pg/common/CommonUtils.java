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
package com.phonepe.sdk.pg.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

@UtilityClass
public class CommonUtils {

    /**
     * Computes the SHA-256 hash of the provided data using the SHA-256 algorithm.
     *
     * @param args the objects to compute the hash for
     * @return the computed hash as a string
     */
    public String calculateSha256(Object... args) {
        List<String> listOfArgs = convertArgsToList(args);
        String data = String.join(":", listOfArgs);
        return shaHex(data, ShaAlgorithm.SHA256);
    }

    public List<String> convertArgsToList(Object... args) {
        List<String> list = new ArrayList<>();
        for (Object arg : args) {
            list.add(arg.toString());
        }
        return list;
    }

    /**
     * Computes the SHA-256 hash of the provided data using the specified algorithm.
     *
     * @param data the data to compute the hash for
     * @param algorithm the SHA algorithm to use (currently only supports SHA-256)
     * @return the computed hash as a hexadecimal string
     */
    public String shaHex(String data, ShaAlgorithm algorithm) {
        switch (algorithm) {
            case SHA256:
                return DigestUtils.sha256Hex(data);
            default:
                return data;
        }
    }

    /** Enum representing the supported SHA algorithms. Currently, only SHA-256 is supported. */
    private enum ShaAlgorithm {
        SHA256;
    }

    public boolean isCallbackValid(String username, String password, String authorization) {
        String sha256hash = calculateSha256(username, password);
        return Objects.equals(sha256hash, authorization);
    }
}
