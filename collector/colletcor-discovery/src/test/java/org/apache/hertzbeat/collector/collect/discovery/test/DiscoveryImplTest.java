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

package org.apache.hertzbeat.collector.collect.discovery.test;

import java.util.ArrayList;
import java.util.List;
import org.apache.hertzbeat.colletcor.collect.DiscoveryImpl;
import org.apache.hertzbeat.colletcor.collect.discovery.DiscoveryClient;
import org.apache.hertzbeat.colletcor.collect.discovery.DiscoveryClientManagement;
import org.apache.hertzbeat.colletcor.collect.discovery.entity.ServerInfo;
import org.apache.hertzbeat.colletcor.collect.discovery.entity.ServiceInstance;
import org.apache.hertzbeat.common.entity.job.Metrics;
import org.apache.hertzbeat.common.entity.job.protocol.DiscoveryProtocol;
import org.apache.hertzbeat.common.entity.message.CollectRep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test case for {@link DiscoveryImpl}
 */
@ExtendWith(MockitoExtension.class)
class DiscoveryImplTest {

    @InjectMocks
    @Spy
    private DiscoveryImpl discovery;

    @Mock
    private DiscoveryClient client;

    @Mock
    private DiscoveryClientManagement discoveryClientManagement;

    @Test
    void testServerCollect() {
        CollectRep.MetricsData.Builder builder = CollectRep.MetricsData.newBuilder();

        String port = "123";
        String host = "127.0.0.1";
        var httpsdProtocol = DiscoveryProtocol.builder()
                .port(port)
                .host(host)
                .discoveryClientTypeName("consul")
                .build();
        List<String> aliasField = new ArrayList<>();
        aliasField.add("address");
        aliasField.add("port");
        aliasField.add("responseTime");
        Metrics metrics = new Metrics();
        metrics.setName("server");
        metrics.setDiscovery(httpsdProtocol);
        metrics.setAliasFields(aliasField);

        Mockito.when(discoveryClientManagement.getClient(httpsdProtocol)).thenReturn(client);
        ServerInfo serverInfo = ServerInfo.builder()
                .address(host)
                .port(port)
                .build();
        Mockito.when(client.getServerInfo()).thenReturn(serverInfo);
        discovery.setDiscoveryClientManagement(discoveryClientManagement);
        discovery.preCheck(metrics);
        discovery.collect(builder, 1L, "test", metrics);
        for (CollectRep.ValueRow valueRow : builder.getValuesList()) {
            Assertions.assertEquals(host, valueRow.getColumns(0));
            Assertions.assertEquals(port, valueRow.getColumns(1));
            Assertions.assertNotNull(valueRow.getColumns(2));
        }
    }

    @Test
    void testServiceCollect() {
        CollectRep.MetricsData.Builder builder = CollectRep.MetricsData.newBuilder();

        String port = "123";
        String host = "127.0.0.1";
        var discoveryProtocol = DiscoveryProtocol.builder()
                .port(port)
                .host(host)
                .discoveryClientTypeName("consul")
                .build();
        List<String> aliasField = new ArrayList<>();
        aliasField.add("serviceId");
        aliasField.add("serviceName");
        aliasField.add("address");
        aliasField.add("port");
        Metrics metrics = new Metrics();
        metrics.setName("service");
        metrics.setDiscovery(discoveryProtocol);
        metrics.setAliasFields(aliasField);

        Mockito.when(discoveryClientManagement.getClient(discoveryProtocol)).thenReturn(client);

        String serviceId = "test";
        String serviceName = "service";
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        serviceInstances.add(ServiceInstance.builder()
                .serviceId(serviceId)
                .serviceName(serviceName)
                .address(host)
                .port(Integer.parseInt(port))
                .build());

        Mockito.when(client.getServices()).thenReturn(serviceInstances);
        discovery.setDiscoveryClientManagement(discoveryClientManagement);
        discovery.preCheck(metrics);
        discovery.collect(builder, 1L, "test", metrics);
        Assertions.assertEquals(builder.getValuesCount(), 1);
        for (CollectRep.ValueRow valueRow : builder.getValuesList()) {
            Assertions.assertEquals(serviceId, valueRow.getColumns(0));
            Assertions.assertEquals(serviceName, valueRow.getColumns(1));
            Assertions.assertEquals(host, valueRow.getColumns(2));
            Assertions.assertEquals(port, valueRow.getColumns(3));
        }
    }

}
