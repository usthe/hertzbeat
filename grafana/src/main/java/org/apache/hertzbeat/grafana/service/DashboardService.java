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

package org.apache.hertzbeat.grafana.service;

import static org.apache.hertzbeat.grafana.common.CommonConstants.CREATE_DASHBOARD_API;
import static org.apache.hertzbeat.grafana.common.CommonConstants.DELETE_DASHBOARD_API;
import static org.apache.hertzbeat.grafana.common.CommonConstants.INSTANCE;
import static org.apache.hertzbeat.grafana.common.CommonConstants.KIOSK;
import static org.apache.hertzbeat.grafana.common.CommonConstants.REFRESH;
import java.util.HashMap;
import java.util.Map;
import org.apache.hertzbeat.common.entity.grafana.GrafanaDashboard;
import org.apache.hertzbeat.common.util.JsonUtil;
import org.apache.hertzbeat.grafana.config.GrafanaConfiguration;
import org.apache.hertzbeat.grafana.dao.DashboardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;


/**
 * Service for managing Grafana dashboards.
 */
@Service
@Slf4j
public class DashboardService {

    @Autowired
    private ServiceAccountService serviceAccountService;

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private GrafanaConfiguration grafanaConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Creates a new dashboard in Grafana.
     *
     * @param dashboardJson the JSON representation of the dashboard
     * @param monitorId the ID of the monitor associated with the dashboard
     * @return ResponseEntity containing the response from Grafana
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> createDashboard(String dashboardJson, Long monitorId) {
        String token = serviceAccountService.getToken();
        String url = grafanaConfiguration.getPrefix() + grafanaConfiguration.getUrl() + CREATE_DASHBOARD_API;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("dashboard", JsonUtil.fromJson(dashboardJson, Object.class));
        body.put("overwrite", true);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                GrafanaDashboard grafanaDashboard = JsonUtil.fromJson(response.getBody(), GrafanaDashboard.class);
                if (grafanaDashboard != null) {
                    grafanaDashboard.setEnabled(true);
                    grafanaDashboard.setUrl(grafanaConfiguration.getPrefix() + grafanaConfiguration.getUrl() + grafanaDashboard.getUrl().replace(grafanaConfiguration.getUrl(), "") + KIOSK + REFRESH + INSTANCE + monitorId);
                    grafanaDashboard.setMonitorId(monitorId);
                    dashboardDao.save(grafanaDashboard);
                    log.info("create dashboard success, token: {}", response.getBody());
                }
                return response;
            } else {
                log.error("create dashboard error: {}", response.getStatusCode());
                throw new RuntimeException("create dashboard error");
            }
        } catch (Exception ex) {
            log.error("create dashboard error", ex);
            throw new RuntimeException("create dashboard error", ex);
        }
    }


    /**
     * Deletes a dashboard in Grafana by monitor ID.
     *
     * @param monitorId the ID of the monitor associated with the dashboard
     * @return ResponseEntity containing the response from Grafana
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> deleteDashboard(Long monitorId) {
        GrafanaDashboard grafanaDashboard = dashboardDao.findByMonitorId(monitorId);
        String token = serviceAccountService.getToken();
        String url = grafanaConfiguration.getPrefix() + grafanaConfiguration.getUrl() + String.format(DELETE_DASHBOARD_API, grafanaDashboard.getUid());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        dashboardDao.deleteByMonitorId(monitorId);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("delete dashboard success");
                return response;
            } else {
                log.error("delete dashboard error: {}", response.getStatusCode());
                throw new RuntimeException("delete dashboard error");
            }

        } catch (Exception ex) {
            log.error("delete dashboard error", ex);
            throw new RuntimeException("delete dashboard error", ex);
        }
    }


    /**
     * Retrieves a dashboard by monitor ID.
     *
     * @param monitorId the ID of the monitor associated with the dashboard
     * @return GrafanaDashboard object
     */
    public GrafanaDashboard getDashboardByMonitorId(Long monitorId) {
        return dashboardDao.findByMonitorId(monitorId);
    }

    /**
     * Disables a Grafana dashboard by monitor ID.
     *
     * @param monitorId the ID of the monitor associated with the dashboard
     */
    public void closeGrafanaDashboard(Long monitorId) {
        GrafanaDashboard grafanaDashboard = dashboardDao.findByMonitorId(monitorId);
        if (grafanaDashboard != null) {
            grafanaDashboard.setEnabled(false);
            dashboardDao.save(grafanaDashboard);
        }
    }

    /**
     * Enables a Grafana dashboard by monitor ID.
     *
     * @param monitorId the ID of the monitor associated with the dashboard
     */
    public void openGrafanaDashboard(Long monitorId) {
        GrafanaDashboard grafanaDashboard = dashboardDao.findByMonitorId(monitorId);
        if (grafanaDashboard != null) {
            grafanaDashboard.setEnabled(true);
            dashboardDao.save(grafanaDashboard);
        }
    }
}
