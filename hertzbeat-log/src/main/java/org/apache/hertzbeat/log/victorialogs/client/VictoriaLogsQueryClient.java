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

package org.apache.hertzbeat.log.victorialogs.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.log.victorialogs.config.VictoriaLogsProperties;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryResponse;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryRequest;
import org.apache.hertzbeat.log.victorialogs.exception.VictoriaLogsQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.web.util.UriUtils;

@Slf4j
@Component
public class VictoriaLogsQueryClient {

    private static final String QUERY = "/select/logsql/query";
    private static final String TAIL = "/select/logsql/tail";
    private static final String HITS = "/select/logsql/hits";
    private static final String STATS_QUERY = "/select/logsql/stats_query";
    private static final String STATS_QUERY_RANGE = "/select/logsql/stats_query_range";
    private static final String STREAMS_IDS = "/select/logsql/streams_ids";
    private static final String STREAMS = "/select/logsql/streams";
    private static final String STREAM_FIELD_NAMES = "/select/logsql/stream_field_names";
    private static final String STREAM_FIELD_VALUES = "/select/logsql/stream_field_values";
    private static final String FIELD_NAMES = "/select/logsql/field_names";
    private static final String FIELD_VALUES = "/select/logsql/field_values";

    private final RestTemplate restTemplate;
    private final VictoriaLogsProperties properties;
    private final ObjectMapper objectMapper;

    @Autowired
    public VictoriaLogsQueryClient(RestTemplateBuilder restTemplateBuilder,
                                   VictoriaLogsProperties properties,
                                   ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ZERO)
                .build();
    }

    /**
     * Execute a regular query
     * @param request Query request containing search parameters
     * @return List of log entries matching the query
     */
    public List<LogQueryResponse> query(LogQueryRequest request) {
        String url = buildQueryUrl(request);
        log.info("Executing query: {}", url);

        try {
            String response = restTemplate.getForObject(url, String.class);
            List<LogQueryResponse> entries = new ArrayList<>();
            if (response != null) {
                String[] lines = response.split("\n");
                for (String line : lines) {
                    if (!StringUtils.hasText(line)) {
                        continue;
                    }
                    entries.add(objectMapper.readValue(line, LogQueryResponse.class));
                }
            }
            return entries;
        } catch (Exception e) {
            log.error("Failed to execute query: {}", url, e);
            throw new VictoriaLogsQueryException("Failed to execute query", e);
        }
    }

    /**
     * Count log entries matching the query
     * @param request Query request containing search parameters
     * @return Number of matching log entries
     */
    public long count(LogQueryRequest request) {
        String url = buildCountUrl(request);
        log.debug("Executing count query: {}", url);

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (StringUtils.hasText(response)) {
                return Long.parseLong(response.trim());
            }
            return 0;
        } catch (Exception e) {
            log.error("Failed to execute count query: {}", url, e);
            throw new VictoriaLogsQueryException("Failed to execute count query", e);
        }
    }

    /**
     * Execute a streaming query for real-time log tailing
     * @param query Query expression
     * @param handler Callback function to handle each log entry
     */
    public void tail(String query, Consumer<LogQueryResponse> handler) {
        String url = buildTailUrl(query);
        log.debug("Starting log tail stream: {}", url);

        try {
            restTemplate.execute(url, HttpMethod.GET, null, response -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!StringUtils.hasText(line)) {
                            continue;
                        }
                        LogQueryResponse entry = objectMapper.readValue(line, LogQueryResponse.class);
                        handler.accept(entry);
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Error in log tail stream: {}", query, e);
            throw new VictoriaLogsQueryException("Failed to tail logs", e);
        }
    }

    /**
     * Build URL for regular queries
     */
    private String buildQueryUrl(LogQueryRequest request) {
        // 不使用 UriComponentsBuilder，因为它可能会导致多次编码
        StringBuilder urlBuilder = new StringBuilder(properties.url())
                .append("/select/logsql/query?query=");

        // 只编码一次查询参数
        urlBuilder.append(URLEncoder.encode(request.getQuery(), StandardCharsets.UTF_8));

        // 添加其他参数
        if (StringUtils.hasText(request.getStart())) {
            urlBuilder.append("&start=").append(URLEncoder.encode(request.getStart(), StandardCharsets.UTF_8));
        }
        if (StringUtils.hasText(request.getEnd())) {
            urlBuilder.append("&end=").append(URLEncoder.encode(request.getEnd(), StandardCharsets.UTF_8));
        }
        if (request.getLimit() != null) {
            urlBuilder.append("&limit=").append(request.getLimit());
        } else {
            urlBuilder.append("&limit=500");
        }

        return urlBuilder.toString();
    }



    /**
     * Build URL for count queries
     */
    private String buildCountUrl(LogQueryRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(properties.url())
                .path("/api/v1/query/count")
                .queryParam("query", request.getQuery());

        if (StringUtils.hasText(request.getStart())) {
            builder.queryParam("start", request.getStart());
        }
        if (StringUtils.hasText(request.getEnd())) {
            builder.queryParam("end", request.getEnd());
        }

        return builder.build().encode().toUriString();
    }

    /**
     * Build URL for streaming queries
     */
    private String buildTailUrl(String query) {
        return UriComponentsBuilder
                .fromHttpUrl(properties.url())
                .path(TAIL)
                .queryParam("query", query)
                .build()
                .encode()
                .toUriString();
    }
}