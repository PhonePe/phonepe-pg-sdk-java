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
package com.phonepe.sdk.pg.payments.v2.customcheckout.filters;

import com.phonepe.sdk.pg.common.http.HttpHeaderPair;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Mutable context passed through the filter chain before a pay request is dispatched.
 * Filters may update the hostUrl or add to the headers list.
 */
@Getter
@Setter
public class PayContext {

	private String hostUrl;
	private List<HttpHeaderPair> headers;
	private String deviceOS;

	public PayContext(String hostUrl, List<HttpHeaderPair> headers, String deviceOS) {
		this.hostUrl = hostUrl;
		this.headers = headers;
		this.deviceOS = deviceOS;
	}
}
