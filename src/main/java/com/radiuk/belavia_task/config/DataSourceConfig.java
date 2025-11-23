package com.radiuk.belavia_task.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Builder;

import javax.sql.DataSource;

@Builder
public class DataSourceConfig {

    public static DataSource createDataSource(String databaseUrl, String username, String password) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(databaseUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);



        return new HikariDataSource(hikariConfig);
    }
}