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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "Log Analysis API")
@RestController
@RequestMapping(path = "/api/logs")
@Validated
@Slf4j
public class LogAnalysisController {

    @Autowired
    private LogAnalysisService logAnalysisService;

    @Operation(summary = "Search logs", description = "Search logs with query parameters")
    @GetMapping(value = "/query", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<List<LogQueryResponse>>> searchLogs(
            @Parameter(description = "Log query expression", required = true)
            @RequestParam @NotBlank String query,
            @Parameter(description = "Start time for the query")
            @RequestParam(required = false) String start,
            @Parameter(description = "End time for the query")
            @RequestParam(required = false) String end,
            @Parameter(description = "Maximum number of results to return")
            @RequestParam(required = false) Integer limit) {
        try {
            LogQueryRequest request = LogQueryRequest.builder()
                    .query(query)
                    .start(start)
                    .end(end)
                    .limit(limit)
                    .build();
            List<LogQueryResponse> entries = logAnalysisService.searchLogs(request);
            return ResponseEntity.ok(Message.success(entries));
        } catch (Exception e) {
            log.error("search logs error", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, e.getMessage()));
        }
    }

    @Operation(summary = "Query log entries by keyword", description = "Search log entries containing specific keyword")
    @GetMapping(value = "/query/keyword", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<List<LogQueryResponse>>> queryByKeyword(
            @Parameter(description = "Keyword to search for", required = true)
            @RequestParam @NotBlank String keyword,
            @Parameter(description = "Start time for the query")
            @RequestParam(required = false) String start,
            @Parameter(description = "End time for the query")
            @RequestParam(required = false) String end,
            @Parameter(description = "Maximum number of results to return")
            @RequestParam(required = false) Integer limit) {
        try {
            List<LogQueryResponse> entries = logAnalysisService.queryByKeyword(keyword, start, end, limit);
            return ResponseEntity.ok(Message.success(entries));
        } catch (Exception e) {
            log.error("query by keyword error", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, e.getMessage()));
        }
    }

    @Operation(summary = "Count keyword occurrences", description = "Count occurrences of a keyword in logs")
    @GetMapping(value = "/count/keyword", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<Long>> countKeywordOccurrences(
            @Parameter(description = "Keyword to count", required = true)
            @RequestParam @NotBlank String keyword,
            @Parameter(description = "Start time for the query")
            @RequestParam(required = false) String start,
            @Parameter(description = "End time for the query")
            @RequestParam(required = false) String end) {
        try {
            long count = logAnalysisService.countKeywordOccurrences(keyword, start, end);
            return ResponseEntity.ok(Message.success(count));
        } catch (Exception e) {
            log.error("count keyword occurrences error", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, e.getMessage()));
        }
    }

    @GetMapping(value = "/tail", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs(@RequestParam String query) {
        SseEmitter emitter = new SseEmitter(0L);
        logAnalysisService.streamLogs(query, emitter);
        return emitter;
    }

    @Operation(summary = "Stop all streams", description = "Stop all active log streams")
    @PostMapping(value = "/tail/stop", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<String>> stopAllStreams() {
        try {
            logAnalysisService.stopAllStreams();
            return ResponseEntity.ok(Message.success("Successfully stopped all streams"));
        } catch (Exception e) {
            log.error("Failed to stop streams", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, "Failed to stop streams"));
        }
    }

    @Operation(summary = "Get active stream count", description = "Get the number of active log streams")
    @GetMapping(value = "/tail/count", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Message<Integer>> getActiveStreamCount() {
        try {
            int count = logAnalysisService.getActiveStreamCount();
            return ResponseEntity.ok(Message.success(count));
        } catch (Exception e) {
            log.error("Failed to get stream count", e);
            return ResponseEntity.ok(Message.fail(FAIL_CODE, "Failed to get stream count"));
        }
    }
}