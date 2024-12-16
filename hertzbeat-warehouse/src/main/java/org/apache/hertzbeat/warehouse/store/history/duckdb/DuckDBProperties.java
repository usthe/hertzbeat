package org.apache.hertzbeat.warehouse.store.history.duckdb;

import org.apache.hertzbeat.common.constants.ConfigConstants;
import org.apache.hertzbeat.common.constants.SignConstants;
import org.apache.hertzbeat.warehouse.constants.WarehouseConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = ConfigConstants.FunctionModuleConstants.WAREHOUSE
        + SignConstants.DOT
        + WarehouseConstants.STORE
        + SignConstants.DOT
        + WarehouseConstants.HistoryName.DUCKDB)
public record DuckDBProperties(@DefaultValue("false") boolean enabled,
@DefaultValue("jdbc:duckdb:./data/hertzbeat.duckdb") String url,
@DefaultValue("org.duckdb.DuckDBDriver") String driverClassName)  {
}
