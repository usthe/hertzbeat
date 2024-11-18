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

package org.apache.hertzbeat.log.victorialogs.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.hertzbeat.common.constants.ConfigConstants;
import org.apache.hertzbeat.common.constants.SignConstants;
import org.apache.hertzbeat.log.victorialogs.model.LogConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * VictoriaLogs Configuration Properties
 *
 */
@ConfigurationProperties(prefix = ConfigConstants.FunctionModuleConstants.LOG
        + SignConstants.DOT
        + LogConstants.VICTORIA_LOGS)
public record VictoriaLogsProperties(
        /**
         * Whether to enable VictoriaLogs integration
         */
        @DefaultValue("false")
        boolean enabled,

        /**
         * VictoriaLogs server URL
         */
        @DefaultValue("http://localhost:9428")
        @NotNull
        String url

) {
    @Valid
    public VictoriaLogsProperties {
        if (enabled && !isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid VictoriaLogs URL: " + url);
        }
    }

    /**
     * Validate URL format
     *
     * @param url URL to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}