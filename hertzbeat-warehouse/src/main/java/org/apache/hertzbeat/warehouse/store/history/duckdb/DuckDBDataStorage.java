package org.apache.hertzbeat.warehouse.store.history.duckdb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hertzbeat.common.constants.CommonConstants;
import org.apache.hertzbeat.common.entity.dto.Value;
import org.apache.hertzbeat.common.entity.message.CollectRep;
import org.apache.hertzbeat.common.util.JsonUtil;
import org.apache.hertzbeat.common.util.SnowFlakeIdGenerator;
import org.apache.hertzbeat.common.util.TimePeriodUtil;
import org.apache.hertzbeat.warehouse.store.history.AbstractHistoryDataStorage;
import org.duckdb.DuckDBConnection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * duckdb data storage
 */
@Primary
@Component
@ConditionalOnProperty(prefix = "warehouse.store.duckdb",
        name = "enabled", havingValue = "true")
@Slf4j
public class DuckDBDataStorage extends AbstractHistoryDataStorage {

    private static final Pattern SQL_SPECIAL_STRING_PATTERN = Pattern.compile("(\\\\)|(')");
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS main.hzb_history (id LONG, monitor_id LONG, app VARCHAR, metrics VARCHAR, metric VARCHAR, instance VARCHAR, metric_type INT, str VARCHAR, int32 INT, dou DOUBLE, ts LONG)";
    private static final String QUERY_HISTORY_WITH_INSTANCE_SQL =
            "SELECT ts, metric_type, instance, str, int32, dou FROM main.hzb_history WHERE monitor_id = %s AND app = %s AND metrics = %s AND metric = %s AND instance = %s AND ts >= now - %s order by ts desc";
    private static final String QUERY_HISTORY_SQL =
            "SELECT * FROM main.hzb_history WHERE monitor_id = %s AND app = %s AND metrics = %s AND metric = %s AND ts >= now - %s order by ts desc";
    private static final String QUERY_HISTORY_INTERVAL_WITH_INSTANCE_SQL =
            "SELECT first(ts), first(`%s`), avg(`%s`), min(`%s`), max(`%s`) FROM `%s` WHERE instance = '%s' AND ts >= now - %s interval(4h)";
    private static final String QUERY_INSTANCE_SQL =
            "SELECT DISTINCT instance FROM `%s` WHERE ts >= now - 1w";

    private final String duckDBUrl;

    public DuckDBDataStorage(DuckDBProperties duckDBProperties) {
        if (duckDBProperties == null) {
            log.error("init error, please config Warehouse DuckDB props in application.yml");
            throw new IllegalArgumentException("please config Warehouse DuckDB props");
        }
        this.serverAvailable = true;
        this.duckDBUrl = duckDBProperties.url();
        initDuckDBDatabase();
    }

    private void initDuckDBDatabase() {
        try {
            DuckDBConnection conn = (DuckDBConnection) DriverManager.getConnection(duckDBUrl);
            conn.prepareStatement(CREATE_TABLE).execute();
        } catch (SQLException e) {
            log.error("DuckDB create table error", e);
            throw new RuntimeException("DuckDB create table error");
        }
    }

    @Override
    public void saveData(CollectRep.MetricsData metricsData) {
        if (!isServerAvailable() || metricsData.getCode() != CollectRep.Code.SUCCESS) {
            return;
        }
        if (metricsData.getValuesList().isEmpty()) {
            log.info("[warehouse duckdb] flush metrics data {} is null, ignore.", metricsData.getId());
            return;
        }        
        List<CollectRep.Field> fieldsList = metricsData.getFieldsList();

        try (DuckDBConnection conn = (DuckDBConnection) DriverManager.getConnection(duckDBUrl);
            var appender = conn.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "hzb_history")) {
            for (CollectRep.ValueRow valueRow : metricsData.getValuesList()) {
                for (int i = 0; i < fieldsList.size(); i++) {
                    CollectRep.Field field = fieldsList.get(i);
                    String columnValue = valueRow.getColumns(i);
                    appender.beginRow();
                    // id
                    appender.append(SnowFlakeIdGenerator.generateId());
                    // monitorId
                    appender.append(metricsData.getId());
                    // app
                    appender.append(metricsData.getApp());
                    // metrics
                    appender.append(metricsData.getMetrics());
                    // metric
                    appender.append(field.getName());
                    // instance
                    Map<String, String> labels = new HashMap<>(8);
                    if (field.getLabel()) {
                        labels.put(field.getName(), columnValue);
                    }
                    appender.append(JsonUtil.toJson(labels));
                    // metricType
                    appender.append(field.getType());
                    if (!CommonConstants.NULL_VALUE.equals(columnValue)) {
                        switch (field.getType()) {
                            // str
                            case CommonConstants.TYPE_STRING:
                                appender.append(formatStringValue(columnValue));
                                appender.append(null);
                                appender.append(null);
                                break;
                            // int32
                            case CommonConstants.TYPE_TIME:
                                appender.append(null);
                                appender.append(Integer.parseInt(columnValue));
                                appender.append(null);
                                break;
                            // dou
                            case CommonConstants.TYPE_NUMBER:
                                appender.append(null);
                                appender.append(null);
                                appender.append(Double.parseDouble(columnValue));
                                break;
                            default:
                                appender.append(null);
                                appender.append(null);
                                appender.append(Double.parseDouble(columnValue));
                                break;
                        }
                    } else {
                        appender.append(null);
                        appender.append(null);
                        appender.append(null);
                    }
                    // time
                    appender.append(metricsData.getTime());
                    appender.endRow();
                }
            }
        } catch (SQLException e) {
            log.error("Error saving data to DuckDB: ", e);
        }
    }

    public Map<String, List<Value>> getHistoryMetricData(Long monitorId, String app, String metrics,
                                                         String metric, String label, String history) {
        Map<String, List<Value>> instanceValuesMap = new HashMap<>(8);
        Connection connection;
        try {
            connection = DriverManager.getConnection(duckDBUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String selectSql = label == null ? String.format(QUERY_HISTORY_SQL, monitorId, app, metrics, metric, history) :
                    String.format(QUERY_HISTORY_WITH_INSTANCE_SQL, monitorId, app, metrics, metric, label, history);
            pstmt = connection.prepareStatement(selectSql);
            rs = pstmt.executeQuery();
            String str;
            Integer int32;
            Double dou;
            while (rs.next()) {
                long ts = rs.getLong("ts");
                int metricType = rs.getByte("metric_type");
                switch (metric) {
                    case CommonConstants.TYPE_STRING:
                        rs.getString("str")
                        break;
                    // int32
                    case CommonConstants.TYPE_TIME:
                        appender.append(null);
                        appender.append(Integer.parseInt(columnValue));
                        appender.append(null);
                        break;
                    // dou
                    case CommonConstants.TYPE_NUMBER:
                        appender.append(null);
                        appender.append(null);
                        appender.append(Double.parseDouble(columnValue));
                        break;
                    default:
                        appender.append(null);
                        appender.append(null);
                        appender.append(Double.parseDouble(columnValue));
                        break;

                }

                if (metricType == CommonConstants.TYPE_NUMBER) {
                    Double douValue = rs.getDouble("dou");
                    if (douValue != null && !rs.wasNull()) {
                        value = BigDecimal.valueOf(douValue)
                                .setScale(4, RoundingMode.HALF_UP)
                                .stripTrailingZeros()
                                .toPlainString();
                    }
                } else {
                    String strValue = rs.getString("str");
                    value = strValue != null ? strValue : "";
                }

                String instanceValue = rs.getString("instance");
                instanceValue = instanceValue != null ? instanceValue : "";

                List<Value> valueList = instanceValuesMap.computeIfAbsent(instanceValue, k -> new LinkedList<>());
                valueList.add(new Value(value, rs.getLong("time")));
            }

        } catch (SQLException e) {
            log.error("Error querying history metric data: ", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.error("Error closing ResultSet: ", e);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    log.error("Error closing PreparedStatement: ", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Error closing Connection: ", e);
                }
            }
        }

        return instanceValuesMap;
    }

    @Override
    public Map<String, List<Value>> getHistoryIntervalMetricData(Long monitorId, String app, String metrics, String metric, String label, String history) {
        return new HashMap<>(8);
    }

    @Override
    public void destroy() throws Exception {
    }

    private String formatStringValue(String value) {
        return SQL_SPECIAL_STRING_PATTERN.matcher(value).replaceAll("\\\\$0");
    }
}