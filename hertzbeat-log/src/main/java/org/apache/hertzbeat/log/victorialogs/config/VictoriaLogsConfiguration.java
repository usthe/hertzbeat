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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hertzbeat.common.constants.ConfigConstants;
import org.apache.hertzbeat.common.constants.SignConstants;
import org.apache.hertzbeat.log.victorialogs.client.VictoriaLogsQueryClient;
import org.apache.hertzbeat.log.victorialogs.model.LogConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * VictoriaLogs configuration class
 */
@Configuration
@EnableConfigurationProperties(VictoriaLogsProperties.class)
public class VictoriaLogsConfiguration {
    /**
     * Creates VictoriaLogs query client
     */
    @Bean
    @ConditionalOnProperty(prefix = ConfigConstants.FunctionModuleConstants.LOG
            + SignConstants.DOT
            + LogConstants.VICTORIA_LOGS, name = "enabled", havingValue = "true")
    public VictoriaLogsQueryClient victoriaLogsQueryClient(
            VictoriaLogsProperties properties,
            RestTemplateBuilder victoriaLogsRestTemplatebuilder,
            ObjectMapper objectMapper) {
        return new VictoriaLogsQueryClient(victoriaLogsRestTemplatebuilder, properties, objectMapper);
    }
}