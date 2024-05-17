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

package org.apache.hertzbeat.collector.collect.http.promethus.exporter;

import org.apache.hertzbeat.common.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test case for {@link ExporterParser}
 */
class ExporterParserTest {

    @Test
    void textToMetric() {
        String resp = "# HELP disk_total_bytes Total space for path\n"
                + "# TYPE disk_total_bytes gauge\n"
                + "disk_total_bytes{path=\"C:\\\\hertzbeat\\\\repo\\\\testpath\",} 4.29496725504E11\n"
                + "# HELP go_gc_cycles_automatic_gc_cycles_total Count of completed GC cycles generated by the Go runtime.\n"
                + "# TYPE go_gc_cycles_automatic_gc_cycles_total counter\n"
                + "go_gc_cycles_automatic_gc_cycles_total 0\n"
                + "# HELP go_gc_cycles_forced_gc_cycles_total Count of completed GC cycles forced by the application.\n"
                + "# TYPE go_gc_cycles_forced_gc_cycles_total counter\n"
                + "go_gc_cycles_forced_gc_cycles_total 0\n"
                + "# HELP go_gc_cycles_total_gc_cycles_total Count of all completed GC cycles.\n"
                + "# TYPE go_gc_cycles_total_gc_cycles_total counter\n"
                + "go_gc_cycles_total_gc_cycles_total 0\n"
                + "# HELP go_gc_duration_seconds A summary of the pause duration of garbage collection cycles.\n"
                + "# TYPE go_gc_duration_seconds summary\n"
                + "go_gc_duration_seconds{quantile=\"0\"} 0\n"
                + "go_gc_duration_seconds{quantile=\"0.25\"} 0\n"
                + "go_gc_duration_seconds{quantile=\"0.5\"} 0\n"
                + "go_gc_duration_seconds{quantile=\"0.75\"} 0\n"
                + "go_gc_duration_seconds{quantile=\"1\"} 0\n"
                + "# TYPE jvm info\n"
                + "# HELP jvm VM version info\n"
                + "jvm_info{runtime=\"OpenJDK Runtime Environment\",vendor=\"Azul Systems, Inc.\",version=\"11.0.13+8-LTS\"} 1.0\n"
                + "# TYPE jvm_gc_collection_seconds summary\n"
                + "# HELP jvm_gc_collection_seconds Time spent in a given JVM garbage collector in seconds.\n"
                + "jvm_gc_collection_seconds_count{gc=\"G1 Young Generation\"} 10.0\n"
                + "jvm_gc_collection_seconds_sum{gc=\"G1 Young Generation\"} 0.051\n"
                + "jvm_gc_collection_seconds_count{gc=\"G1 Old Generation\"} 0.0\n"
                + "jvm_gc_collection_seconds_sum{gc=\"G1 Old Generation\"} 0.0\n"
                + "# TYPE resource_group_aggregate_usage_secs summary\n"
                + "resource_group_aggregate_usage_secs{cluster=\"standalone\",quantile=\"0.5\"} 2.69245E-4\n"
                + "resource_group_aggregate_usage_secs{cluster=\"standalone\",quantile=\"0.9\"} 3.49601E-4\n"
                + "resource_group_aggregate_usage_secs_count{cluster=\"standalone\"} 13.0\n"
                + "resource_group_aggregate_usage_secs_sum{cluster=\"standalone\"} 0.004832498\n"
                + "resource_group_aggregate_usage_secs_created{cluster=\"standalone\"} 1.715842140749E9\n"
                + "# TYPE metadata_store_ops_latency_ms histogram\n"
                + "metadata_store_ops_latency_ms_bucket{cluster=\"standalone\",name=\"metadata-store\",type=\"get\",status=\"success\",le=\"1.0\"} 59.0\n"
                + "metadata_store_ops_latency_ms_bucket{cluster=\"standalone\",name=\"metadata-store\",type=\"get\",status=\"success\",le=\"3.0\"} 61.0\n"
                + "metadata_store_ops_latency_ms_bucket{cluster=\"standalone\",name=\"metadata-store\",type=\"get\",status=\"success\",le=\"5.0\"} 61.0\n"
                + "# EOF";

        ExporterParser parser = new ExporterParser();
        Map<String, MetricFamily> metricFamilyMap = parser.textToMetric(resp);
        assertEquals(5, metricFamilyMap.get("resource_group_aggregate_usage_secs").getMetricList().size());
        assertEquals(3, metricFamilyMap.get("metadata_store_ops_latency_ms").getMetricList().size());
        assertEquals(5, metricFamilyMap.get("go_gc_duration_seconds").getMetricList().size());
        assertEquals(9, metricFamilyMap.size());
    }
}