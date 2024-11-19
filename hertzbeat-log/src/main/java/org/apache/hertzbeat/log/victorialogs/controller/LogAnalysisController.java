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

package org.apache.hertzbeat.log.victorialogs.controller;

import static org.apache.hertzbeat.common.constants.CommonConstants.FAIL_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.entity.dto.Message;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryResponse;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryRequest;
import org.apache.hertzbeat.log.victorialogs.service.LogAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import reactor.core.publisher.Flux;

@Tag(name = "Log Analysis API")
@RestController
@RequestMapping(path = "/api/logs")
@Validated
@Slf4j
public class LogAnalysisController {

    @Autowired
    private LogAnalysisService logAnalysisService;

    @Operation(summary = "Search logs", description = "Search logs with query parameters")
    @PostMapping(value = "/query", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<List<LogQueryResponse>>> searchLogs(
            @Parameter(description = "Log query parameters", required = true)
            @RequestBody @Validated LogQueryRequest request) {
        try {
            List<LogQueryResponse> entries = logAnalysisService.searchLogs(request);
            return ResponseEntity.ok(Message.success(entries));
        } catch (Exception e) {
            log.error("search logs error", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, e.getMessage()));
        }
    }

    @Operation(summary = "Query log entries by keyword", description = "Search log entries containing specific keyword")
    @PostMapping(value = "/query/keyword", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<List<LogQueryResponse>>> queryByKeyword(
            @Parameter(description = "Log query parameters", required = true)
            @RequestBody @Validated LogQueryRequest request) {
        try {
            List<LogQueryResponse> entries = logAnalysisService.queryByKeyword(
                    request.getQuery(),
                    request.getStart(),
                    request.getEnd(),
                    request.getLimit());
            return ResponseEntity.ok(Message.success(entries));
        } catch (Exception e) {
            log.error("query by keyword error", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, e.getMessage()));
        }
    }

    @Operation(summary = "Count keyword occurrences", description = "Count occurrences of a keyword in logs")
    @PostMapping(value = "/count/keyword", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<Long>> countKeywordOccurrences(
            @Parameter(description = "Log query parameters", required = true)
            @RequestBody @Validated LogQueryRequest request) {
        try {
            long count = logAnalysisService.countKeywordOccurrences(
                    request.getQuery(),
                    request.getStart(),
                    request.getEnd());
            return ResponseEntity.ok(Message.success(count));
        } catch (Exception e) {
            log.error("count keyword occurrences error", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, e.getMessage()));
        }
    }

    @PostMapping(value = "/tail", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<LogQueryResponse>> streamLogs(
            @Parameter(description = "Log query parameters", required = true)
            @RequestBody @Validated LogQueryRequest request) {
        return logAnalysisService.streamLogs(request.getQuery());
    }

}