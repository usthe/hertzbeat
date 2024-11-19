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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.log.victorialogs.config.VictoriaLogsProperties;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryResponse;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryRequest;
import org.apache.hertzbeat.log.victorialogs.exception.VictoriaLogsQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;

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
    private final WebClient webClient;

    @Autowired
    public VictoriaLogsQueryClient(RestTemplateBuilder restTemplateBuilder,
                                   VictoriaLogsProperties properties,
                                   ObjectMapper objectMapper,
                                   WebClient webClient) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ZERO)
                .build();
        this.webClient = webClient;
    }

    /**
     * Execute a regular query
     * @param request Query request containing search parameters
     * @return List of log entries matching the query
     */
    public List<LogQueryResponse> query(LogQueryRequest request) {
        String url = properties.url() + QUERY;
        log.info("Executing query: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("query", request.getQuery());
            if (StringUtils.hasText(request.getStart())) {
                params.add("start", request.getStart());
            }
            if (StringUtils.hasText(request.getEnd())) {
                params.add("end", request.getEnd());
            }
            params.add("limit", String.valueOf(request.getLimit() != null ? request.getLimit() : 500));

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            String response = restTemplate.postForObject(url, requestEntity, String.class);

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
        String url = properties.url() + "/api/v1/query/count";
        log.debug("Executing count query: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("query", request.getQuery());
            if (StringUtils.hasText(request.getStart())) {
                params.add("start", request.getStart());
            }
            if (StringUtils.hasText(request.getEnd())) {
                params.add("end", request.getEnd());
            }

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            String response = restTemplate.postForObject(url, requestEntity, String.class);

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
     */
    public Flux<LogQueryResponse> tail(String query) {
        String url = properties.url() + TAIL;

        return webClient
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("query", query))
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(response -> log.debug("Received response: {}", response))
                .filter(StringUtils::hasText)
                .<LogQueryResponse>handle((line, sink) -> {
                    try {
                        sink.next(objectMapper.readValue(line, LogQueryResponse.class));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse log entry: {}", line, e);
                        sink.error(new VictoriaLogsQueryException("Failed to parse log entry", e));
                    }
                })
                .doOnSubscribe(subscription -> log.debug("Starting subscription to log stream"))
                .doOnError(error -> log.error("Error in log tail stream: {}", error.getMessage(), error))
                .doOnComplete(() -> log.debug("Log stream completed"))
                .onErrorMap(e -> new VictoriaLogsQueryException("Failed to tail logs", e));
    }

    /**
     * Execute a streaming query for real-time log tailing with heartbeat mechanism
     * @param query Query expression
     */
    public Flux<ServerSentEvent<LogQueryResponse>> tailAsSSE(String query) {
        // Create a heartbeat event flux that emits every 15 seconds
        Flux<ServerSentEvent<LogQueryResponse>> heartbeat = Flux.interval(Duration.ZERO, Duration.ofSeconds(15))
                .map(i -> ServerSentEvent.<LogQueryResponse>builder()
                        .event("heartbeat")
                        .data(null)
                        .build());

        // Create the initial connection event
        Flux<ServerSentEvent<LogQueryResponse>> connectionEvent = Flux.just(
                ServerSentEvent.<LogQueryResponse>builder()
                        .event("connected")
                        .data(null)
                        .build()
        );

        // Combine the data stream with heartbeat events
        return Flux.merge(
                connectionEvent,
                heartbeat,
                tail(query).map(logResponse -> ServerSentEvent.<LogQueryResponse>builder()
                        .event("log")
                        .data(logResponse)
                        .build())
        );
    }

}