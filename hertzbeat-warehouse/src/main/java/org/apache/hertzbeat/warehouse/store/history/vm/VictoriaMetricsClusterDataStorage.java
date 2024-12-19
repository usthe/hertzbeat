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

package org.apache.hertzbeat.warehouse.store.history.vm;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hertzbeat.common.constants.CommonConstants;
import org.apache.hertzbeat.common.constants.MetricDataConstants;
import org.apache.hertzbeat.common.constants.NetworkConstants;
import org.apache.hertzbeat.common.constants.SignConstants;
import org.apache.hertzbeat.common.entity.arrow.reader.ArrowVectorReader;
import org.apache.hertzbeat.common.entity.arrow.reader.ArrowVectorReaderImpl;
import org.apache.hertzbeat.common.entity.arrow.RowWrapper;
import org.apache.hertzbeat.common.entity.dto.Value;
import org.apache.hertzbeat.common.entity.message.CollectRep;
import org.apache.hertzbeat.common.util.CommonUtil;
import org.apache.hertzbeat.common.util.JsonUtil;
import org.apache.hertzbeat.common.util.TimePeriodUtil;
import org.apache.hertzbeat.warehouse.store.history.AbstractHistoryDataStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.apache.hertzbeat.common.constants.ConfigConstants.FunctionModuleConstants.STATUS;

/**
 * tdengine data storage
 */
@Primary
@Component
@ConditionalOnProperty(prefix = "warehouse.store.victoria-metrics.cluster", name = "enabled", havingValue = "true")
@Slf4j
public class VictoriaMetricsClusterDataStorage extends AbstractHistoryDataStorage {

    private static final String IMPORT_PATH = "/api/v1/import";
    private static final String EXPORT_PATH = "/api/v1/export";
    private static final String STATUS_PATH = "/api/v1/status/tsdb";
    private static final String STATUS_SUCCESS = "success";
    private static final String QUERY_RANGE_PATH = "/api/v1/query_range";
    private static final String LABEL_KEY_NAME = "__name__";
    private static final String LABEL_KEY_JOB = "job";
    private static final String LABEL_KEY_INSTANCE = "instance";
    private static final String SPILT = "_";
    private static final String MONITOR_METRICS_KEY = "__metrics__";
    private static final String MONITOR_METRIC_KEY = "__metric__";

    private final VictoriaMetricsInsertProperties vmInsertProps;
    private final VictoriaMetricsSelectProperties vmSelectProps;

    private final RestTemplate restTemplate;

    public VictoriaMetricsClusterDataStorage(VictoriaMetricsClusterProperties vmClusterProps,
                                             RestTemplate restTemplate) {
        if (vmClusterProps == null) {
            log.error("init error, please config Warehouse victoriaMetrics cluster props in application.yml");
            throw new IllegalArgumentException("please config Warehouse victoriaMetrics cluster props");
        }
        this.restTemplate = restTemplate;
        this.vmInsertProps = vmClusterProps.insert();
        this.vmSelectProps = vmClusterProps.select();
        serverAvailable = checkVictoriaMetricsDatasourceAvailable();
    }

    private boolean checkVictoriaMetricsDatasourceAvailable() {
        // check server status
        try {
            String result = restTemplate.getForObject(vmSelectProps.url() + STATUS_PATH, String.class);

            JsonNode jsonNode = JsonUtil.fromJson(result);
            if (jsonNode != null && STATUS_SUCCESS.equalsIgnoreCase(jsonNode.get(STATUS).asText())) {
                return true;
            }
            log.error("check victoria metrics cluster server status not success: {}.", result);
        } catch (Exception e) {
            log.error("check victoria metrics cluster server status error: {}.", e.getMessage());
        }
        return false;
    }

    @Override
    public void saveData(CollectRep.MetricsData metricsData) {
        if (!isServerAvailable()) {
            serverAvailable = checkVictoriaMetricsDatasourceAvailable();
        }
        if (!isServerAvailable() || metricsData.getCode() != CollectRep.Code.SUCCESS) {
            return;
        }
        if (metricsData.getData().isEmpty()) {
            log.info("[warehouse victoria-metrics] flush metrics data {} {} {} is null, ignore.",
                    metricsData.getId(), metricsData.getApp(), metricsData.getMetrics());
            return;
        }
        Map<String, String> defaultLabels = Maps.newHashMapWithExpectedSize(8);
        defaultLabels.put(MONITOR_METRICS_KEY, metricsData.getMetrics());
        boolean isPrometheusAuto;
        if (metricsData.getApp().startsWith(CommonConstants.PROMETHEUS_APP_PREFIX)) {
            isPrometheusAuto = true;
            defaultLabels.remove(MONITOR_METRICS_KEY);
            defaultLabels.put(LABEL_KEY_JOB, metricsData.getApp()
                    .substring(CommonConstants.PROMETHEUS_APP_PREFIX.length()));
        } else {
            isPrometheusAuto = false;
            defaultLabels.put(LABEL_KEY_JOB, metricsData.getApp());
        }
        defaultLabels.put(LABEL_KEY_INSTANCE, String.valueOf(metricsData.getId()));


        try (ArrowVectorReader arrowVectorReader = new ArrowVectorReaderImpl(metricsData.getData().toByteArray())) {
            List<Field> fieldList = arrowVectorReader.getAllFields();
            Long[] timestamp = new Long[]{metricsData.getTime()};
            Map<String, Double> fieldsValue = Maps.newHashMapWithExpectedSize(fieldList.size());
            Map<String, String> labels = Maps.newHashMapWithExpectedSize(fieldList.size());
            List<VictoriaMetricsDataStorage.VictoriaMetricsContent> contentList = new LinkedList<>();


            RowWrapper rowWrapper = arrowVectorReader.readRow();
            while (rowWrapper.hasNextRow()) {
                rowWrapper = rowWrapper.nextRow();
                fieldsValue.clear();
                labels.clear();

                rowWrapper.cellStream().forEach(cell -> {
                    String value = cell.getValue();
                    Byte type = cell.getMetadataAsByte(MetricDataConstants.TYPE);
                    Boolean label = cell.getMetadataAsBoolean(MetricDataConstants.LABEL);

                    if (type == CommonConstants.TYPE_NUMBER && !label) {
                        // number metrics data
                        if (!CommonConstants.NULL_VALUE.equals(value)) {
                            fieldsValue.put(cell.getField().getName(), CommonUtil.parseStrDouble(value));
                        }
                    }
                    // label
                    if (label && !CommonConstants.NULL_VALUE.equals(value)) {
                        labels.put(cell.getField().getName(), value);
                    }

                    for (Map.Entry<String, Double> entry : fieldsValue.entrySet()) {
                        if (entry.getKey() != null && entry.getValue() != null) {
                            try {
                                labels.putAll(defaultLabels);
                                String labelName = isPrometheusAuto ? metricsData.getMetrics()
                                        : metricsData.getMetrics() + SPILT + entry.getKey();
                                labels.put(LABEL_KEY_NAME, labelName);
                                if (!isPrometheusAuto) {
                                    labels.put(MONITOR_METRIC_KEY, entry.getKey());
                                }
                                VictoriaMetricsDataStorage.VictoriaMetricsContent content = VictoriaMetricsDataStorage.VictoriaMetricsContent.builder()
                                        .metric(new HashMap<>(labels))
                                        .values(new Double[]{entry.getValue()})
                                        .timestamps(timestamp)
                                        .build();
                                contentList.add(content);
                            } catch (Exception e) {
                                log.error("combine metrics data error: {}.", e.getMessage(), e);
                            }

                        }
                    }
                });
            }


            if (contentList.isEmpty()) {
                log.info("[warehouse victoria-metrics] flush metrics data {} is empty, ignore.", metricsData.getId());
                return;
            }


            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (StringUtils.hasText(vmInsertProps.username())
                        && StringUtils.hasText(vmInsertProps.password())) {
                    String authStr = vmInsertProps.username() + ":" + vmInsertProps.password();
                    String encodedAuth = new String(Base64.encodeBase64(authStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                    headers.add(HttpHeaders.AUTHORIZATION,  NetworkConstants.BASIC + SignConstants.BLANK + encodedAuth);
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (VictoriaMetricsDataStorage.VictoriaMetricsContent content : contentList) {
                    stringBuilder.append(JsonUtil.toJson(content)).append("\n");
                }
                HttpEntity<String> httpEntity = new HttpEntity<>(stringBuilder.toString(), headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(vmInsertProps.url() + IMPORT_PATH,
                        httpEntity, String.class);
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    log.debug("insert metrics data to victoria-metrics success.");
                } else {
                    log.error("insert metrics data to victoria-metrics failed. {}", responseEntity.getBody());
                }
            } catch (Exception e){
                log.error("flush metrics data to victoria-metrics error: {}.", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("flush metrics data to victoria-metrics error: {}.", e.getMessage(), e);
        }

    }

    @Override
    public void destroy() {
    }

    @Override
    public Map<String, List<Value>> getHistoryMetricData(Long monitorId, String app, String metrics, String metric,
            String label, String history) {
        String labelName = metrics + SPILT + metric;
        if (CommonConstants.PROMETHEUS.equals(app)) {
            labelName = metrics;
        }
        String timeSeriesSelector =
                LABEL_KEY_NAME + "=\"" + labelName + "\"" + "," + LABEL_KEY_INSTANCE + "=\"" + monitorId + "\"" + ","
                        + MONITOR_METRIC_KEY + "=\"" + metric + "\"";
        Map<String, List<Value>> instanceValuesMap = new HashMap<>(8);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            if (StringUtils.hasText(vmSelectProps.username()) && StringUtils.hasText(vmSelectProps.password())) {
                String authStr = vmSelectProps.username() + ":" + vmSelectProps.password();
                String encodedAuth = new String(Base64.encodeBase64(authStr.getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8);
                headers.add(HttpHeaders.AUTHORIZATION, NetworkConstants.BASIC
                        + SignConstants.BLANK + encodedAuth);
            }
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            URI uri = UriComponentsBuilder.fromHttpUrl(vmSelectProps.url() + EXPORT_PATH)
                    .queryParam(URLEncoder.encode("match[]", StandardCharsets.UTF_8),
                            URLEncoder.encode("{" + timeSeriesSelector + "}", StandardCharsets.UTF_8))
                    .queryParam("start", URLEncoder.encode("now-" + history, StandardCharsets.UTF_8))
                    .queryParam("end", "now").build(true).toUri();
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                    String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.debug("query metrics data from victoria-metrics success. {}", uri);
                if (StringUtils.hasText(responseEntity.getBody())) {
                    String[] contentJsonArr = responseEntity.getBody().split("\n");
                    List<VictoriaMetricsContent> contents = Arrays.stream(contentJsonArr)
                            .map(item -> JsonUtil.fromJson(item, VictoriaMetricsContent.class)).toList();
                    for (VictoriaMetricsContent content : contents) {
                        Map<String, String> labels = content.getMetric();
                        labels.remove(LABEL_KEY_NAME);
                        labels.remove(LABEL_KEY_JOB);
                        labels.remove(LABEL_KEY_INSTANCE);
                        labels.remove(MONITOR_METRICS_KEY);
                        labels.remove(MONITOR_METRIC_KEY);
                        String labelStr = JsonUtil.toJson(labels);
                        if (content.getValues() != null && content.getTimestamps() != null) {
                            List<Value> valueList = instanceValuesMap.computeIfAbsent(labelStr,
                                    k -> new LinkedList<>());
                            if (content.getValues().length != content.getTimestamps().length) {
                                log.error("content.getValues().length != content.getTimestamps().length");
                                continue;
                            }
                            Double[] values = content.getValues();
                            Long[] timestamps = content.getTimestamps();
                            for (int index = 0; index < content.getValues().length; index++) {
                                String strValue = BigDecimal.valueOf(values[index]).setScale(4, RoundingMode.HALF_UP)
                                        .stripTrailingZeros().toPlainString();
                                // read timestamp here is ms unit
                                valueList.add(new Value(strValue, timestamps[index]));
                            }
                        }
                    }
                }
            } else {
                log.error("query metrics data from victoria-metrics failed. {}", responseEntity);
            }
        } catch (Exception e) {
            log.error("query metrics data from victoria-metrics error. {}.", e.getMessage(), e);
        }
        return instanceValuesMap;
    }

    @Override
    public Map<String, List<Value>> getHistoryIntervalMetricData(Long monitorId, String app, String metrics,
            String metric, String label, String history) {
        if (!serverAvailable) {
            log.error("""
                    
                    \t---------------VictoriaMetrics Init Failed---------------
                    \t--------------Please Config VictoriaMetrics--------------
                    \t----------Can Not Use Metric History Now----------
                    """);
            return Collections.emptyMap();
        }
        long endTime = ZonedDateTime.now().toEpochSecond();
        long startTime;
        try {
            if (NumberUtils.isParsable(history)) {
                startTime = NumberUtils.toLong(history);
                startTime = (ZonedDateTime.now().toEpochSecond() - startTime);
            } else {
                TemporalAmount temporalAmount = TimePeriodUtil.parseTokenTime(history);
                ZonedDateTime dateTime = ZonedDateTime.now().minus(temporalAmount);
                startTime = dateTime.toEpochSecond();
            }
        } catch (Exception e) {
            log.error("history time error: {}. use default: 6h", e.getMessage());
            ZonedDateTime dateTime = ZonedDateTime.now().minus(Duration.ofHours(6));
            startTime = dateTime.toEpochSecond();
        }
        String labelName = metrics + SPILT + metric;
        if (CommonConstants.PROMETHEUS.equals(app)) {
            labelName = metrics;
        }
        String timeSeriesSelector =
                LABEL_KEY_NAME + "=\"" + labelName + "\"" + "," + LABEL_KEY_INSTANCE + "=\"" + monitorId + "\"" + ","
                        + MONITOR_METRIC_KEY + "=\"" + metric + "\"";
        Map<String, List<Value>> instanceValuesMap = new HashMap<>(8);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            if (StringUtils.hasText(vmSelectProps.username()) && StringUtils.hasText(vmSelectProps.password())) {
                String authStr = vmSelectProps.username() + ":" + vmSelectProps.password();
                String encodedAuth = new String(Base64.encodeBase64(authStr.getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8);
                headers.add(HttpHeaders.AUTHORIZATION, NetworkConstants.BASIC
                        + SignConstants.BLANK + encodedAuth);
            }
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            URI uri = UriComponentsBuilder.fromHttpUrl(vmSelectProps.url() + QUERY_RANGE_PATH)
                    .queryParam(URLEncoder.encode("query", StandardCharsets.UTF_8),
                            URLEncoder.encode("{" + timeSeriesSelector + "}", StandardCharsets.UTF_8))
                    .queryParam("step", "4h").queryParam("start", startTime).queryParam("end", endTime).build(true)
                    .toUri();
            ResponseEntity<PromQlQueryContent> responseEntity = restTemplate.exchange(uri, HttpMethod.GET,
                    httpEntity, PromQlQueryContent.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.debug("query metrics data from victoria-metrics success. {}", uri);
                if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null
                        && responseEntity.getBody().getData().getResult() != null) {
                    List<PromQlQueryContent.ContentData.Content> contents = responseEntity.getBody().getData()
                            .getResult();
                    for (PromQlQueryContent.ContentData.Content content : contents) {
                        Map<String, String> labels = content.getMetric();
                        labels.remove(LABEL_KEY_NAME);
                        labels.remove(LABEL_KEY_JOB);
                        labels.remove(LABEL_KEY_INSTANCE);
                        labels.remove(MONITOR_METRICS_KEY);
                        labels.remove(MONITOR_METRIC_KEY);
                        String labelStr = JsonUtil.toJson(labels);
                        if (content.getValues() != null && !content.getValues().isEmpty()) {
                            List<Value> valueList = instanceValuesMap.computeIfAbsent(labelStr,
                                    k -> new LinkedList<>());
                            for (Object[] valueArr : content.getValues()) {
                                long timestamp = Long.parseLong(String.valueOf(valueArr[0]));
                                String value = new BigDecimal(String.valueOf(valueArr[1])).setScale(4,
                                        RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                                // read timestamp here is s unit
                                valueList.add(new Value(value, timestamp * 1000));
                            }
                        }
                    }
                }
            } else {
                log.error("query metrics data from victoria-metrics failed. {}", responseEntity);
            }
            // max
            uri = UriComponentsBuilder.fromHttpUrl(vmSelectProps.url() + QUERY_RANGE_PATH)
                    .queryParam(URLEncoder.encode("query", StandardCharsets.UTF_8),
                            URLEncoder.encode("max_over_time({" + timeSeriesSelector + "})", StandardCharsets.UTF_8))
                    .queryParam("step", "4h").queryParam("start", startTime).queryParam("end", endTime).build(true)
                    .toUri();
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, PromQlQueryContent.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null
                        && responseEntity.getBody().getData().getResult() != null) {
                    List<PromQlQueryContent.ContentData.Content> contents = responseEntity.getBody().getData()
                            .getResult();
                    for (PromQlQueryContent.ContentData.Content content : contents) {
                        Map<String, String> labels = content.getMetric();
                        labels.remove(LABEL_KEY_NAME);
                        labels.remove(LABEL_KEY_JOB);
                        labels.remove(LABEL_KEY_INSTANCE);
                        labels.remove(MONITOR_METRICS_KEY);
                        labels.remove(MONITOR_METRIC_KEY);
                        String labelStr = JsonUtil.toJson(labels);
                        if (content.getValues() != null && !content.getValues().isEmpty()) {
                            List<Value> valueList = instanceValuesMap.computeIfAbsent(labelStr,
                                    k -> new LinkedList<>());
                            if (valueList.size() == content.getValues().size()) {
                                for (int timestampIndex = 0; timestampIndex < valueList.size(); timestampIndex++) {
                                    Value value = valueList.get(timestampIndex);
                                    Object[] valueArr = content.getValues().get(timestampIndex);
                                    String maxValue = new BigDecimal(String.valueOf(valueArr[1])).setScale(4,
                                            RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                                    value.setMax(maxValue);
                                }
                            }
                        }
                    }
                }
            }
            // min
            uri = UriComponentsBuilder.fromHttpUrl(vmSelectProps.url() + QUERY_RANGE_PATH)
                    .queryParam(URLEncoder.encode("query", StandardCharsets.UTF_8),
                            URLEncoder.encode("min_over_time({" + timeSeriesSelector + "})", StandardCharsets.UTF_8))
                    .queryParam("step", "4h").queryParam("start", startTime).queryParam("end", endTime).build(true)
                    .toUri();
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, PromQlQueryContent.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null
                        && responseEntity.getBody().getData().getResult() != null) {
                    List<PromQlQueryContent.ContentData.Content> contents = responseEntity.getBody().getData()
                            .getResult();
                    for (PromQlQueryContent.ContentData.Content content : contents) {
                        Map<String, String> labels = content.getMetric();
                        labels.remove(LABEL_KEY_NAME);
                        labels.remove(LABEL_KEY_JOB);
                        labels.remove(LABEL_KEY_INSTANCE);
                        labels.remove(MONITOR_METRICS_KEY);
                        labels.remove(MONITOR_METRIC_KEY);
                        String labelStr = JsonUtil.toJson(labels);
                        if (content.getValues() != null && !content.getValues().isEmpty()) {
                            List<Value> valueList = instanceValuesMap.computeIfAbsent(labelStr,
                                    k -> new LinkedList<>());
                            if (valueList.size() == content.getValues().size()) {
                                for (int timestampIndex = 0; timestampIndex < valueList.size(); timestampIndex++) {
                                    Value value = valueList.get(timestampIndex);
                                    Object[] valueArr = content.getValues().get(timestampIndex);
                                    String minValue = new BigDecimal(String.valueOf(valueArr[1])).setScale(4,
                                            RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                                    value.setMin(minValue);
                                }
                            }
                        }
                    }
                }
            }
            // avg
            uri = UriComponentsBuilder.fromHttpUrl(vmSelectProps.url() + QUERY_RANGE_PATH)
                    .queryParam(URLEncoder.encode("query", StandardCharsets.UTF_8),
                            URLEncoder.encode("avg_over_time({" + timeSeriesSelector + "})", StandardCharsets.UTF_8))
                    .queryParam("step", "4h").queryParam("start", startTime).queryParam("end", endTime).build(true)
                    .toUri();
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, PromQlQueryContent.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null
                        && responseEntity.getBody().getData().getResult() != null) {
                    List<PromQlQueryContent.ContentData.Content> contents = responseEntity.getBody().getData()
                            .getResult();
                    for (PromQlQueryContent.ContentData.Content content : contents) {
                        Map<String, String> labels = content.getMetric();
                        labels.remove(LABEL_KEY_NAME);
                        labels.remove(LABEL_KEY_JOB);
                        labels.remove(LABEL_KEY_INSTANCE);
                        labels.remove(MONITOR_METRICS_KEY);
                        labels.remove(MONITOR_METRIC_KEY);
                        String labelStr = JsonUtil.toJson(labels);
                        if (content.getValues() != null && !content.getValues().isEmpty()) {
                            List<Value> valueList = instanceValuesMap.computeIfAbsent(labelStr,
                                    k -> new LinkedList<>());
                            if (valueList.size() == content.getValues().size()) {
                                for (int timestampIndex = 0; timestampIndex < valueList.size(); timestampIndex++) {
                                    Value value = valueList.get(timestampIndex);
                                    Object[] valueArr = content.getValues().get(timestampIndex);
                                    String avgValue = new BigDecimal(String.valueOf(valueArr[1])).setScale(4,
                                            RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                                    value.setMean(avgValue);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("query metrics data from victoria-metrics error. {}.", e.getMessage(), e);
        }
        return instanceValuesMap;
    }

    /**
     * victoria metrics content
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class VictoriaMetricsContent {

        /**
         * metric contains metric name plus labels for a particular time series
         */
        private Map<String, String> metric;

        /**
         * values contains raw sample values for the given time series
         */
        private Double[] values;

        /**
         * timestamps contains raw sample UNIX timestamps in milliseconds for the given time series
         * every timestamp is associated with the value at the corresponding position
         */
        private Long[] timestamps;
    }
}
