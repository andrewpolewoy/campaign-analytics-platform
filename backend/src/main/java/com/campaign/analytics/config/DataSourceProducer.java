package com.campaign.analytics.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

@ApplicationScoped
public class DataSourceProducer {

    @Produces
    @ApplicationScoped
    public DataSource dataSource() {
        String url = env("DB_URL");
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        if (url != null && !url.isBlank()) {
            dataSource.setUrl(url);
        } else {
            String host = envOrDefault("DB_HOST", "localhost");
            String port = envOrDefault("DB_PORT", "5432");
            String database = envOrDefault("DB_NAME", "campaign");
            dataSource.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        }
        dataSource.setUser(envOrDefault("DB_USER", "analytics"));
        dataSource.setPassword(envOrDefault("DB_PASSWORD", "analytics"));
        return dataSource;
    }

    private static String env(String key) {
        return System.getenv(key);
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
