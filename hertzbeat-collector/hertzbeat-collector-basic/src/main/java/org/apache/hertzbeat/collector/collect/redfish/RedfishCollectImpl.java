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

package org.apache.hertzbeat.collector.collect.redfish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.collector.collect.AbstractCollect;
import org.apache.hertzbeat.common.entity.arrow.MetricsDataBuilder;
import org.apache.hertzbeat.collector.collect.common.cache.CacheIdentifier;
import org.apache.hertzbeat.collector.collect.common.cache.ConnectionCommonCache;
import org.apache.hertzbeat.collector.collect.redfish.cache.RedfishConnect;
import org.apache.hertzbeat.collector.dispatch.DispatchConstants;
import org.apache.hertzbeat.collector.util.JsonPathParser;
import org.apache.hertzbeat.common.constants.CommonConstants;
import org.apache.hertzbeat.common.entity.job.Metrics;
import org.apache.hertzbeat.common.entity.job.protocol.RedfishProtocol;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * redfish collect impl
 */
@Slf4j
public class RedfishCollectImpl extends AbstractCollect {

    private final ConnectionCommonCache<CacheIdentifier, RedfishConnect> connectionCommonCache;

    public RedfishCollectImpl() {
        connectionCommonCache = new ConnectionCommonCache<>();
    }

    @Override
    public void preCheck(Metrics metrics) throws IllegalArgumentException {
        if (metrics == null || metrics.getRedfish() == null) {
            throw new IllegalArgumentException("Redfish collect must has redfish params");
        }
        RedfishProtocol redfishProtocol = metrics.getRedfish();
        Assert.hasText(redfishProtocol.getHost(), "Redfish Protocol host is required.");
        Assert.hasText(redfishProtocol.getPort(), "Redfish Protocol port is required.");
        Assert.hasText(redfishProtocol.getUsername(), "Redfish Protocol username is required.");
        Assert.hasText(redfishProtocol.getPassword(), "Redfish Protocol password is required.");
    }

    @Override
    public void collect(MetricsDataBuilder metricsDataBuilder, Metrics metrics) {
        ConnectSession connectSession;
        try {
            connectSession = getRedfishConnectSession(metrics.getRedfish());
        } catch (Exception e) {
            log.error("Redfish session create error: {}", e.getMessage());
            metricsDataBuilder.setFailedMsg(e.getMessage());
            return;
        }
        List<String> resourcesUri = getResourcesUri(metrics, connectSession);
        if (resourcesUri == null || resourcesUri.isEmpty()) {
            metricsDataBuilder.setFailedMsg("Get redfish resources uri error");
            return;
        }
        for (String uri : resourcesUri) {
            String resp;
            try {
                resp = connectSession.getRedfishResource(uri);
            } catch (Exception e) {
                log.error("Get redfish {} detail resource error: {}", uri, e.getMessage());
                continue;
            }
            parseRedfishResource(metricsDataBuilder, resp, metrics);
        }
    }

    private ConnectSession getRedfishConnectSession(RedfishProtocol redfishProtocol) throws Exception {
        CacheIdentifier identifier = CacheIdentifier.builder()
                .ip(redfishProtocol.getHost())
                .port(redfishProtocol.getPort())
                .password(redfishProtocol.getPassword())
                .username(redfishProtocol.getUsername())
                .build();
        ConnectSession redfishConnectSession = null;
        Optional<RedfishConnect> cacheOption = connectionCommonCache.getCache(identifier, true);
        if (cacheOption.isPresent()) {
            RedfishConnect redfishConnect = cacheOption.get();
            redfishConnectSession = redfishConnect.getConnection();
            if (redfishConnectSession == null || !redfishConnectSession.isOpen()) {
                redfishConnectSession = null;
                connectionCommonCache.removeCache(identifier);
            }
        }
        if (redfishConnectSession != null) {
            return redfishConnectSession;
        }
        RedfishClient redfishClient = RedfishClient.create(redfishProtocol);
        redfishConnectSession = redfishClient.connect();
        connectionCommonCache.addCache(identifier, new RedfishConnect(redfishConnectSession));
        return redfishConnectSession;
    }

    @Override
    public String supportProtocol() {
        return DispatchConstants.PROTOCOL_REDFISH;
    }


    private List<String> getResourcesUri(Metrics metrics, ConnectSession connectSession) {
        String name = metrics.getName();
        String collectionSchema = metrics.getRedfish().getSchema();
        String schema = (collectionSchema != null) ? collectionSchema : RedfishCollectionSchema.getSchema(name);
        if (!StringUtils.hasText(schema)) {
            return null;
        }
        String pattern = "\\{\\w+}";
        Pattern r = Pattern.compile(pattern);
        String[] fragment = r.split(schema);
        List<String> res = new ArrayList<>();
        for (String value : fragment) {
            List<String> temp = new ArrayList<>();
            if (res.isEmpty()) {
                res.add(value);
            } else {
                res = res.stream().map(s -> s + value).collect(Collectors.toList());
            }

            for (String s : res) {
                List<String> t = getCollectionResource(s, connectSession);
                temp.addAll(t);
            }
            res.clear();
            res.addAll(temp);
        }
        return res;
    }

    private List<String> parseCollectionResource(String resp) {
        if (!StringUtils.hasText(resp)) {
            return Collections.emptyList();
        }
        String resourceIdPath = "$.Members[*].['@odata.id']";
        List<Object> resourceIds = JsonPathParser.parseContentWithJsonPath(resp, resourceIdPath);
        return resourceIds.stream().filter(Objects::nonNull).map(String::valueOf).toList();
    }

    private List<String> getCollectionResource(String uri, ConnectSession connectSession) {
        String resp;
        try {
            resp = connectSession.getRedfishResource(uri);
        } catch (Exception e) {
            log.error("Get redfish {} collection resource error: {}", uri, e.getMessage());
            return Collections.emptyList();
        }
        return parseCollectionResource(resp);
    }

    private void parseRedfishResource(MetricsDataBuilder metricsDataBuilder, String resp, Metrics metrics) {
        if (!StringUtils.hasText(resp)) {
            return;
        }

        List<String> jsonPaths = metrics.getRedfish().getJsonPath();
        for (int index = 0; index < jsonPaths.size(); index++) {
            List<Object> res = JsonPathParser.parseContentWithJsonPath(resp, jsonPaths.get(index));
            if (res != null && !res.isEmpty()) {
                Object value = res.get(0);
                metricsDataBuilder.getArrowVectorWriter().setValue(metrics.getAliasFields().get(index),
                        value == null ? CommonConstants.NULL_VALUE : String.valueOf(value));
            } else {
                metricsDataBuilder.getArrowVectorWriter().setNull(metrics.getAliasFields().get(index));
            }
        }
    }
}
