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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogQueryResponse {

    /**
     * Timestamp of the log entry
     */
    @JsonProperty("_time")
    private String timestamp;

    /**
     * Log message content
     */
    @JsonProperty("_msg")
    private String message;

    /**
     * Unique identifier for the log stream
     */
    @JsonProperty("_stream_id")
    private String streamId;

    /**
     * Stream metadata in JSON format
     */
    @JsonProperty("_stream")
    private String stream;

    /**
     * Log level (e.g., INFO, ERROR)
     */
    @JsonProperty("severity")
    private String level;

    /**
     * Name of the service generating the log
     */
    @JsonProperty("service.name")
    private String serviceName;

    /**
     * Additional properties not covered by standard fields
     */
    @Builder.Default
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * Getter for additional properties
     *
     * @return Map of additional properties
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * Setter for additional properties. Captures any unmapped JSON properties.
     *
     * @param key Property key
     * @param value Property value
     */
    @JsonAnySetter
    public void addAdditionalProperty(String key, Object value) {
        if (!isStandardField(key)) {
            additionalProperties.put(key, value);
        }
    }

    /**
     * Check if a field is one of the standard mapped fields
     *
     * @param fieldName Name of the field to check
     * @return true if field is standard, false otherwise
     */
    private boolean isStandardField(String fieldName) {
        return fieldName.equals("_time") ||
                fieldName.equals("_msg") ||
                fieldName.equals("_stream_id") ||
                fieldName.equals("_stream") ||
                fieldName.equals("severity") ||
                fieldName.equals("service.name");
    }
}