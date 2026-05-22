package com.campaign.analytics.service;

import java.io.InputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import com.campaign.analytics.repository.MetricsRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CsvImportService {

    @Inject
    private DataSource dataSource;

    @Inject
    private MetricsRepository metricsRepository;

    public long importImpressions(InputStream csv) throws Exception {

        try (Connection conn = dataSource.getConnection()) {

            CopyManager copyManager =
                    new CopyManager(conn.unwrap(BaseConnection.class));

            String copySql = """
                        COPY impression (
                            reg_time,
                            uid,
                            fc_imp_chk,
                            fc_time_chk,
                            utmtr,
                            mm_dma,
                            os_name,
                            model,
                            hardware,
                            site_id
                        )
                        FROM STDIN
                        WITH (
                            FORMAT csv,
                            HEADER true
                        )
                    """;

            return copyManager.copyIn(copySql, csv);
        }
    }

    public long importEvents(InputStream csv) throws Exception {

        try (Connection conn = dataSource.getConnection()) {

            CopyManager copyManager =
                    new CopyManager(conn.unwrap(BaseConnection.class));

            String copySql = """
                        COPY impression_event (
                            uid,
                            raw_tag
                        )
                        FROM STDIN
                        WITH (
                            FORMAT csv,
                            HEADER true
                        )
                    """;

            return copyManager.copyIn(copySql, csv);
        }
    }

    public void fullImportAndRefresh(
            InputStream impressionsCsv,
            InputStream eventsCsv
    ) throws Exception {

        importImpressions(impressionsCsv);
        importEvents(eventsCsv);
        metricsRepository.refreshMaterializedViews();
    }
}