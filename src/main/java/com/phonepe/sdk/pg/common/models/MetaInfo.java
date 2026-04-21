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
package com.phonepe.sdk.pg.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class MetaInfo {

	/** Free-text field. Max 256 characters. */
	private String udf1;

	/** Free-text field. Max 256 characters. */
	private String udf2;

	/** Free-text field. Max 256 characters. */
	private String udf3;

	/** Free-text field. Max 256 characters. */
	private String udf4;

	/** Free-text field. Max 256 characters. */
	private String udf5;

	/** Free-text field. Max 256 characters. */
	private String udf6;

	/** Free-text field. Max 256 characters. */
	private String udf7;

	/** Free-text field. Max 256 characters. */
	private String udf8;

	/** Free-text field. Max 256 characters. */
	private String udf9;

	/** Free-text field. Max 256 characters. */
	private String udf10;

	/** Alphanumeric + [_ - @ . +] only. Max 50 characters. */
	private String udf11;

	/** Alphanumeric + [_ - @ . +] only. Max 50 characters. */
	private String udf12;

	/** Alphanumeric + [_ - @ . +] only. Max 50 characters. */
	private String udf13;

	/** Alphanumeric + [_ - @ . +] only. Max 50 characters. */
	private String udf14;

	/** Alphanumeric + [_ - @ . +] only. Max 50 characters. */
	private String udf15;

	// Lombok fills in all field setters; only build() is customised here.
	public static class MetaInfoBuilder {

		private static final Pattern RESTRICTED_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\- @.+]*$");

		public MetaInfo build() {
			validateSize("udf1", udf1, 256);
			validateSize("udf2", udf2, 256);
			validateSize("udf3", udf3, 256);
			validateSize("udf4", udf4, 256);
			validateSize("udf5", udf5, 256);
			validateSize("udf6", udf6, 256);
			validateSize("udf7", udf7, 256);
			validateSize("udf8", udf8, 256);
			validateSize("udf9", udf9, 256);
			validateSize("udf10", udf10, 256);
			validateSizeAndPattern("udf11", udf11, 50);
			validateSizeAndPattern("udf12", udf12, 50);
			validateSizeAndPattern("udf13", udf13, 50);
			validateSizeAndPattern("udf14", udf14, 50);
			validateSizeAndPattern("udf15", udf15, 50);
			return new MetaInfo(udf1, udf2, udf3, udf4, udf5,
					udf6, udf7, udf8, udf9, udf10,
					udf11, udf12, udf13, udf14, udf15);
		}

		private static void validateSize(String field, String value, int max) {
			if (value != null && value.length() > max) {
				throw new IllegalArgumentException(
						field + " exceeds maximum allowed size of " + max + " characters");
			}
		}

		private static void validateSizeAndPattern(String field, String value, int max) {
			if (value == null) {
				return;
			}
			if (value.length() > max) {
				throw new IllegalArgumentException(
						field + " exceeds maximum allowed size of " + max + " characters");
			}
			if (!RESTRICTED_PATTERN.matcher(value)
					.matches()) {
				throw new IllegalArgumentException(field
						+ " should only contain alphanumeric characters, underscores, hyphens, spaces, @, ., and +");
			}
		}
	}
}
