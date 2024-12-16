package org.apache.hertzbeat.warehouse.store.history.duckdb;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.hertzbeat.common.entity.dto.Value;
import org.apache.hertzbeat.common.entity.message.CollectRep;
import org.apache.hertzbeat.warehouse.store.history.AbstractHistoryDataStorage;
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

    private static final String CONSTANTS_URL_PREFIX = "jdbc:TAOS-RS://";
    private static final Pattern SQL_SPECIAL_STRING_PATTERN = Pattern.compile("(\\\\)|(')");
    private static final String INSTANCE_NULL = "''";
    private static final String CONSTANTS_CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS %s";
    private static final String INSERT_TABLE_DATA_SQL = "INSERT INTO `%s` USING `%s` TAGS (%s) VALUES %s";
    private static final String CREATE_SUPER_TABLE_SQL = "CREATE STABLE IF NOT EXISTS `%s` %s TAGS (monitor BIGINT)";
    private static final String NO_SUPER_TABLE_ERROR = "Table does not exist";

    private static final String QUERY_HISTORY_WITH_INSTANCE_SQL =
            "SELECT ts, instance, `%s` FROM `%s` WHERE instance = '%s' AND ts >= now - %s order by ts desc";
    private static final String QUERY_HISTORY_SQL =
            "SELECT ts, instance, `%s` FROM `%s` WHERE ts >= now - %s order by ts desc";
    private static final String QUERY_HISTORY_INTERVAL_WITH_INSTANCE_SQL =
            "SELECT first(ts), first(`%s`), avg(`%s`), min(`%s`), max(`%s`) FROM `%s` WHERE instance = '%s' AND ts >= now - %s interval(4h)";
    private static final String QUERY_INSTANCE_SQL =
            "SELECT DISTINCT instance FROM `%s` WHERE ts >= now - 1w";

    private static final String TABLE_NOT_EXIST = "Table does not exist";

    private static final String HERTZBEAT = "hertzbeat";

    public DuckDBDataStorage(DuckDBProperties duckDBProperties) {
        if (duckDBProperties == null) {
            log.error("init error, please config Warehouse DuckDB props in application.yml");
            throw new IllegalArgumentException("please config Warehouse DuckDB props");
        }
    }

    private HikariDataSource hikariDataSource;

    /**
     * init duckdb data storage
     *
     * @param duckDBProperties duckdb properties
     */
    private void initDuckDBDatabase(final DuckDBProperties duckDBProperties) throws SQLException {
        try (
                final Connection tempConnection = DriverManager.getConnection(duckDBProperties.url())
        ) {
            tempConnection.prepareStatement(String.format(CONSTANTS_CREATE_DATABASE, HERTZBEAT))
                    .execute();
        }
    }

    /**
     * query history range metrics data from time-series db
     *
     * @param monitorId monitor id
     * @param app       monitor type
     * @param metrics   metrics
     * @param metric    metric
     * @param label     label
     * @param history   range
     * @return metrics data
     */
    @Override
    public Map<String, List<Value>> getHistoryMetricData(Long monitorId, String app, String metrics, String metric, String label, String history) {
        return Map.of();
    }

    /**
     * query history range interval metrics data from time-series db
     * max min mean metrics value
     *
     * @param monitorId monitor id
     * @param app       monitor type
     * @param metrics   metrics
     * @param metric    metric
     * @param label     label
     * @param history   history range
     * @return metrics data
     */
    @Override
    public Map<String, List<Value>> getHistoryIntervalMetricData(Long monitorId, String app, String metrics, String metric, String label, String history) {
        return Map.of();
    }

    /**
     * save metrics data
     *
     * @param metricsData metrics data
     */
    @Override
    public void saveData(CollectRep.MetricsData metricsData) {

    }

    @Override
    public void destroy() throws Exception {

    }
}
