/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hertzbeat.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;


/**
 * Alarm Statistics Information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Alarm Statistics Information")
public class AlertSummary {

    @Schema(title = "Total number of alerts (including processed and unprocessed alerts)",
            example = "134", accessMode = READ_ONLY)
    private long total;

    @Schema(title = "Number of alerts handled",
            example = "34", accessMode = READ_ONLY)
    private long dealNum;

    @Schema(title = "Alarm handling rate",
            example = "39.34", accessMode = READ_ONLY)
    private float rate;

    @Schema(title = "Number of alarms whose alarm severity is warning alarms (referring to unhandled alarms)",
            example = "43", accessMode = READ_ONLY)
    private long priorityWarningNum;

    @Schema(title = "Number of alarms whose alarm severity is critical alarms (referring to unhandled alarms)",
            example = "56", accessMode = READ_ONLY)
    private long priorityCriticalNum;

    @Schema(title = "Number of alarms whose alarm severity is urgent alarms (referring to unhandled alarms)",
            example = "23", accessMode = READ_ONLY)
    private long priorityEmergencyNum;
}
