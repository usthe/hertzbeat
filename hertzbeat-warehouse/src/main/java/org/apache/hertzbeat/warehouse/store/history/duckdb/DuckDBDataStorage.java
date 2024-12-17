package org.apache.hertzbeat.warehouse.store.history.duckdb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private static final String CONSTANTS_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS hzb_history (monitorId LONG, app STRING, metrics STRING, time LONG, metric STRING, metricType INT, dou DOUBLE, str STRING, int32 INT, instance STRING)";
    private static final String INSERT_TABLE_DATA_SQL = "INSERT INTO hzb_history (monitorId, app, metrics, time, metric, metricType, dou, str, int32, instance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String QUERY_HISTORY_WITH_INSTANCE_SQL = "SELECT time, instance, %s FROM hzb_history WHERE instance = '%s' AND time >= ? ORDER BY time DESC";
    private static final String QUERY_HISTORY_SQL = "SELECT time, instance, %s FROM hzb_history WHERE time >= ? ORDER BY time DESC";

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
            conn.prepareStatement(CONSTANTS_CREATE_TABLE).execute();
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

        try (
                DuckDBConnection conn = (DuckDBConnection) DriverManager.getConnection(duckDBUrl);
                var appender = conn.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "hzb_history")) {
            for (CollectRep.ValueRow valueRow : metricsData.getValuesList()) {
                for (int i = 0; i < fieldsList.size(); i++) {
                    CollectRep.Field field = fieldsList.get(i);
                    String columnValue = valueRow.getColumns(i);
                    appender.beginRow();
                    // TODO: id
                    appender.append(metricsData.getId());
                    appender.append(metricsData.getApp());
                    appender.append(metricsData.getMetrics());
                    // metric
                    appender.append(field.getName());
                    // instance
                    Map<String, String> labels = new HashMap<>(8);

                    if (field.getLabel()) {
                        labels.put(field.getName(), columnValue);
                    }
                    appender.append(JsonUtil.toJson(labels));
                    appender.append(field.getType());
                    if (CommonConstants.NULL_VALUE.equals(columnValue)) {
                        switch (field.getType()) {
                            case CommonConstants.TYPE_NUMBER:
                                appender.append(Double.parseDouble(columnValue));
                                appender.append(null);
                                appender.append(null);
                                break;
                            case CommonConstants.TYPE_STRING:
                                appender.append(null);
                                appender.append(formatStringValue(columnValue));
                                appender.append(null);
                                break;
                            case CommonConstants.TYPE_TIME:
                                appender.append(null);
                                appender.append(null);
                                appender.append(Integer.parseInt(columnValue));
                                break;
                            default:
                                appender.append(Double.parseDouble(columnValue));
                                appender.append(null);
                                appender.append(null);
                                break;
                        }
                    }
                    appender.append(metricsData.getTime());

                    appender.append(JsonUtil.toJson(new HashMap<>()));
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

            // Build the base SQL query with correct column names
            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM hzb_history WHERE monitorId = ? AND app = ? AND metrics = ? AND metric = ?");

            // Add optional conditions
            if (StringUtils.isNotBlank(label)) {
                sql.append(" AND instance = ?");
            }

            // Add time condition if history parameter is provided
            if (history != null) {
                try {
                    TemporalAmount temporalAmount = TimePeriodUtil.parseTokenTime(history);
                    ZonedDateTime dateTime = ZonedDateTime.now().minus(temporalAmount);
                    long timeBefore = dateTime.toEpochSecond() * 1000L;
                    sql.append(" AND time >= ?");
                } catch (Exception e) {
                    log.error("Error parsing history time: {}", e.getMessage());
                }
            }

            // Add ordering
            sql.append(" ORDER BY time DESC");

            // Prepare statement and set parameters
            pstmt = connection.prepareStatement(sql.toString());
            int paramIndex = 1;
            pstmt.setLong(paramIndex++, monitorId);
            pstmt.setString(paramIndex++, app);
            pstmt.setString(paramIndex++, metrics);
            pstmt.setString(paramIndex++, metric);

            if (StringUtils.isNotBlank(label)) {
                pstmt.setString(paramIndex++, label);
            }

            if (history != null) {
                try {
                    TemporalAmount temporalAmount = TimePeriodUtil.parseTokenTime(history);
                    ZonedDateTime dateTime = ZonedDateTime.now().minus(temporalAmount);
                    long timeBefore = dateTime.toEpochSecond() * 1000L;
                    pstmt.setLong(paramIndex, timeBefore);
                } catch (Exception e) {
                    log.error("Error setting time parameter: {}", e.getMessage());
                }
            }

            // Execute query and process results
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String value = "";
                int metricType = rs.getInt("metricType"); // Assuming the column name is metricType

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
            // Close resources in reverse order
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
        // Placeholder implementation, adjust based on requirements
        return new HashMap<>(8);
    }

    @Override
    public void destroy() throws Exception {
    }

    private String formatStringValue(String value) {
        return SQL_SPECIAL_STRING_PATTERN.matcher(value).replaceAll("\\\\$0");
    }
}