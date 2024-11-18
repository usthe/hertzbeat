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

package org.apache.hertzbeat.log.victorialogs.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;


/**
 * Request parameters for log query
 */
@Data
@Builder
public class LogQueryRequest {
    /**
     * Query string for log search
     */
    @NotBlank(message = "Query cannot be empty")
    private String query;

    /**
     * Start time for log search
     * Format: ISO-8601 or relative time like "-1h", "-30m"
     */
    private String start;

    /**
     * End time for log search
     * Format: ISO-8601 or relative time like "now"
     */
    private String end;

    /**
     * Maximum number of results to return
     */
    @Min(value = 1, message = "Limit must be greater than 0")
    @Max(value = 10000, message = "Limit cannot exceed 10000")
    private Integer limit;

    /**
     * Field to order results by
     */
    private String orderBy;

    /**
     * Sort order direction
     */
    private Boolean ascending;

    /**
     * Specific fields to return in the response
     */
    private Set<String> fields;
}
