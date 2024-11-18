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
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
@Slf4j
public class LogAnalysisService {

    private final Set<SseEmitter> activeEmitters = ConcurrentHashMap.newKeySet();
    private final VictoriaLogsQueryClient victoriaLogsQueryClient;
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    private static final long HEARTBEAT_DELAY = 15; // seconds

    @Autowired
    public LogAnalysisService(VictoriaLogsQueryClient victoriaLogsQueryClient) {
        this.victoriaLogsQueryClient = victoriaLogsQueryClient;
    }

    @PreDestroy
    public void destroy() {
        stopAllStreams();
        heartbeatExecutor.shutdown();
        try {
            if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                heartbeatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            heartbeatExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
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

    public void streamLogs(String query, SseEmitter emitter) {
        activeEmitters.add(emitter);

        // Start heartbeat for this emitter
        ScheduledFuture<?> heartbeatFuture = startHeartbeat(emitter);

        CompletableFuture.runAsync(() -> {
            try {
                // Send initial connection established event
                sendEvent(emitter, "open", "Connection established", null);

                victoriaLogsQueryClient.tail(query, logQueryResponse -> {
                    try {
                        if (activeEmitters.contains(emitter)) {
                            sendEvent(emitter, "log", logQueryResponse, String.valueOf(System.currentTimeMillis()));
                        }
                    } catch (IOException e) {
                        handleEmitterError(emitter, heartbeatFuture, e);
                    }
                });
            } catch (Exception e) {
                handleEmitterError(emitter, heartbeatFuture, e);
            }
        });

        // Set up completion callback
        emitter.onCompletion(() -> {
            log.debug("SSE completed");
            cleanup(emitter, heartbeatFuture);
        });

        // Set up timeout callback
        emitter.onTimeout(() -> {
            log.debug("SSE timeout");
            cleanup(emitter, heartbeatFuture);
        });

        // Set up error callback
        emitter.onError(ex -> {
            log.error("SSE error", ex);
            cleanup(emitter, heartbeatFuture);
        });
    }

    private ScheduledFuture<?> startHeartbeat(SseEmitter emitter) {
        return heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                if (activeEmitters.contains(emitter)) {
                    emitter.send(SseEmitter.event()
                            .comment("heartbeat")
                            .build());
                }
            } catch (IOException e) {
                log.warn("Failed to send heartbeat, closing connection", e);
                removeEmitter(emitter);
            }
        }, HEARTBEAT_DELAY, HEARTBEAT_DELAY, TimeUnit.SECONDS);
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object data, String id) throws IOException {
        SseEmitter.SseEventBuilder builder = SseEmitter.event()
                .name(eventName)
                .data(data);

        if (id != null) {
            builder.id(id);
        }

        emitter.send(builder.build());
    }

    private void handleEmitterError(SseEmitter emitter, ScheduledFuture<?> heartbeatFuture, Exception e) {
        log.error("Error in log streaming", e);
        cleanup(emitter, heartbeatFuture);
        try {
            sendEvent(emitter, "error", e.getMessage(), null);
        } catch (IOException ex) {
            log.error("Failed to send error message", ex);
        }
    }

    private void cleanup(SseEmitter emitter, ScheduledFuture<?> heartbeatFuture) {
        removeEmitter(emitter);
        if (heartbeatFuture != null) {
            heartbeatFuture.cancel(true);
        }
    }

    public void removeEmitter(SseEmitter emitter) {
        activeEmitters.remove(emitter);
    }

    public int getActiveStreamCount() {
        return activeEmitters.size();
    }

    public void stopAllStreams() {
        activeEmitters.forEach(emitter -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.error("Error completing emitter", e);
            }
        });
        activeEmitters.clear();
    }
}