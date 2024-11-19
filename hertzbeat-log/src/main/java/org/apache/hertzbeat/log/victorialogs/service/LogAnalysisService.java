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

package org.apache.hertzbeat.log.victorialogs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.log.victorialogs.client.VictoriaLogsQueryClient;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryResponse;
import org.apache.hertzbeat.log.victorialogs.model.LogQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class LogAnalysisService {

    private final VictoriaLogsQueryClient victoriaLogsQueryClient;

    @Autowired
    public LogAnalysisService(VictoriaLogsQueryClient victoriaLogsQueryClient) {
        this.victoriaLogsQueryClient = victoriaLogsQueryClient;
    }


    public List<LogQueryResponse> searchLogs(LogQueryRequest request) {
        return victoriaLogsQueryClient.query(request);
    }

    public List<LogQueryResponse> queryByKeyword(String keyword, String start, String end, Integer limit) {
        LogQueryRequest request = LogQueryRequest.builder()
                .query(buildKeywordQuery(keyword))
                .start(start)
                .end(end)
                .limit(limit)
                .build();
        return victoriaLogsQueryClient.query(request);
    }

    public long countKeywordOccurrences(String keyword, String start, String end) {
        LogQueryRequest request = LogQueryRequest.builder()
                .query(buildKeywordQuery(keyword))
                .start(start)
                .end(end)
                .build();
        return victoriaLogsQueryClient.count(request);
    }

    private String buildKeywordQuery(String keyword) {
        String escapedKeyword = keyword.replace("\"", "\\\"");
        return String.format("message ILIKE \"%%%s%%\"", escapedKeyword);
    }

    public Flux<ServerSentEvent<LogQueryResponse>> streamLogs(String query) {
        return victoriaLogsQueryClient.tailAsSSE(query);
    }


}