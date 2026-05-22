package com.campaign.analytics.service;

import com.campaign.analytics.repository.MetricsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class CsvImportServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static DataSource dataSource;
    private static CsvImportService csvImportService;
    private static MetricsRepository metricsRepository;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource = new TestDataSource();
        createTables();

        metricsRepository = new MetricsRepository();
        injectDataSource(metricsRepository, dataSource);

        csvImportService = new CsvImportService();
        injectDataSource(csvImportService, dataSource);
        injectMetricsRepository(csvImportService, metricsRepository);
    }

    static class TestDataSource implements DataSource {
        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(
                    postgres.getJdbcUrl(),
                    postgres.getUsername(),
                    postgres.getPassword()
            );
        }
        @Override public Connection getConnection(String username, String password) { return null; }
        @Override public java.io.PrintWriter getLogWriter() { return null; }
        @Override public void setLogWriter(java.io.PrintWriter out) { }
        @Override public void setLoginTimeout(int seconds) { }
        @Override public int getLoginTimeout() { return 0; }
        @Override public java.util.logging.Logger getParentLogger() { return null; }
        @Override public <T> T unwrap(Class<T> iface) { return null; }
        @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    }

    private static void injectDataSource(Object target, DataSource ds) {
        try {
            var field = target.getClass().getDeclaredField("dataSource");
            field.setAccessible(true);
            field.set(target, ds);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject DataSource", e);
        }
    }

    private static void injectMetricsRepository(CsvImportService service, MetricsRepository repo) {
        try {
            var field = CsvImportService.class.getDeclaredField("metricsRepository");
            field.setAccessible(true);
            field.set(service, repo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject MetricsRepository", e);
        }
    }

    private static void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS impression (
                    reg_time TIMESTAMP,
                    uid UUID,
                    fc_imp_chk INTEGER,
                    fc_time_chk INTEGER,
                    utmtr INTEGER,
                    mm_dma INTEGER,
                    os_name VARCHAR(64),
                    model VARCHAR(128),
                    hardware VARCHAR(64),
                    site_id VARCHAR(255)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS impression_event (
                    uid UUID,
                    tag VARCHAR(255)
                )
            """);
        }
    }

    @Test
    void testContainerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldImportImpressions_WithValidCsv() throws Exception {
        String csv = "reg_time,uid,fc_imp_chk,fc_time_chk,utmtr,mm_dma,os_name,model,hardware,site_id\n" +
                "2024-01-01,123e4567-e89b-12d3-a456-426614174000,0,0,0,501,Windows,ModelX,Desktop,example.com\n";

        InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        long count = csvImportService.importImpressions(inputStream);

        assertEquals(1, count);
    }

    @Test
    void shouldImportEvents_WithValidCsv() throws Exception {
        String csv = "uid,tag\n" +
                "123e4567-e89b-12d3-a456-426614174000,fclick\n";

        InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        long count = csvImportService.importEvents(inputStream);

        assertEquals(1, count);
    }

    @Test
    void shouldHandleEmptyCsv_ForImpressions() throws Exception {
        String csv = "reg_time,uid,fc_imp_chk,fc_time_chk,utmtr,mm_dma,os_name,model,hardware,site_id\n";

        InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        long count = csvImportService.importImpressions(inputStream);

        assertEquals(0, count);
    }

    @Test
    void shouldHandleEmptyCsv_ForEvents() throws Exception {
        String csv = "uid,tag\n";

        InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        long count = csvImportService.importEvents(inputStream);

        assertEquals(0, count);
    }

    @Test
    void shouldHandleMultipleRows() throws Exception {
        String csv = """
            reg_time,uid,fc_imp_chk,fc_time_chk,utmtr,mm_dma,os_name,model,hardware,site_id
            2024-01-01,11111111-1111-1111-1111-111111111111,0,0,0,501,Windows,ModelX,Desktop,site1.com
            2024-01-02,22222222-2222-2222-2222-222222222222,1,1,1,502,Mac,ModelY,Laptop,site2.com
            2024-01-03,33333333-3333-3333-3333-333333333333,2,2,2,503,Linux,ModelZ,Desktop,site3.com
            """;

        InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        long count = csvImportService.importImpressions(inputStream);

        assertEquals(3, count);
    }
}