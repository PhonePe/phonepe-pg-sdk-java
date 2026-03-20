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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public record MetaInfo(
					@Size(max = 256, message = "Max allowed size is 256") String udf1,
					@Size(max = 256, message = "Max allowed size is 256") String udf2,
					@Size(max = 256, message = "Max allowed size is 256") String udf3,
					@Size(max = 256, message = "Max allowed size is 256") String udf4,
					@Size(max = 256, message = "Max allowed size is 256") String udf5,
					@Size(max = 256, message = "Max allowed size is 256") String udf6,
					@Size(max = 256, message = "Max allowed size is 256") String udf7,
					@Size(max = 256, message = "Max allowed size is 256") String udf8,
					@Size(max = 256, message = "Max allowed size is 256") String udf9,
					@Size(max = 256, message = "Max allowed size is 256") String udf10,
					@Pattern(
							regexp = "^[a-zA-Z0-9_\\- @.+]+$", message = "udf11 should only contain alphanumeric characters, underscores, hyphens, spaces, @, ., and +") @Size(max = 50, message = "Max allowed size is 50") String udf11,
					@Pattern(
							regexp = "^[a-zA-Z0-9_\\- @.+]+$", message = "udf12 should only contain alphanumeric characters, underscores, hyphens, spaces, @, ., and +") @Size(max = 50, message = "Max allowed size is 50") String udf12,
					@Pattern(
							regexp = "^[a-zA-Z0-9_\\- @.+]+$", message = "udf13 should only contain alphanumeric characters, underscores, hyphens, spaces, @, ., and +") @Size(max = 50, message = "Max allowed size is 50") String udf13,
					@Pattern(
							regexp = "^[a-zA-Z0-9_\\- @.+]+$", message = "udf14 should only contain alphanumeric characters, underscores, hyphens, spaces, @, ., and +") @Size(max = 50, message = "Max allowed size is 50") String udf14,
					@Pattern(
							regexp = "^[a-zA-Z0-9_\\- @.+]+$", message = "udf15 should only contain alphanumeric characters, underscores, hyphens, spaces, @, ., and +") @Size(max = 50, message = "Max allowed size is 50") String udf15) {}
