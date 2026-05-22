package com.campaign.analytics.repository;

import com.campaign.analytics.dto.DmaMetricRow;
import com.campaign.analytics.dto.SiteMetricRow;
import com.campaign.analytics.dto.TimeseriesMetricRow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class MetricsRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static DataSource dataSource;
    private static MetricsRepository repository;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource = new TestDataSource();
        createTestSchema();

        repository = new MetricsRepository();
        injectDataSource(repository, dataSource);
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

    private static void injectDataSource(MetricsRepository repository, DataSource ds) {
        try {
            var field = MetricsRepository.class.getDeclaredField("dataSource");
            field.setAccessible(true);
            field.set(repository, ds);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject DataSource", e);
        }
    }

    private static void createTestSchema() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS impression (
                    uid UUID PRIMARY KEY,
                    reg_time TIMESTAMP,
                    site_id VARCHAR(255),
                    mm_dma INTEGER
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS impression_event (
                    uid UUID,
                    tag VARCHAR(255),
                    normalized_tag VARCHAR(255)
                )
            """);

            stmt.execute("""
                CREATE OR REPLACE VIEW v_event_normalized AS
                SELECT uid, tag as raw_tag, 
                    CASE tag 
                        WHEN 'fclick' THEN 'click'
                        WHEN 'vregistration' THEN 'registration'
                        ELSE tag 
                    END as normalized_tag
                FROM impression_event
            """);

            stmt.execute("""
                CREATE MATERIALIZED VIEW mv_timeseries AS
                SELECT 
                    '2024-01-01'::timestamp as metric_time,
                    'click' as normalized_tag,
                    100 as impressions,
                    50 as clicks,
                    60 as events,
                    50.0 as ctr,
                    600.0 as evpm
            """);

            stmt.execute("""
                CREATE MATERIALIZED VIEW mv_site AS
                SELECT 
                    'test.com' as site_id,
                    'click' as normalized_tag,
                    100 as impressions,
                    50 as clicks,
                    60 as events,
                    50.0 as ctr,
                    600.0 as evpm
            """);

            stmt.execute("""
                CREATE MATERIALIZED VIEW mv_dma AS
                SELECT 
                    501 as mm_dma,
                    'registration' as normalized_tag,
                    100 as impressions,
                    50 as clicks,
                    60 as events,
                    50.0 as ctr,
                    600.0 as evpm
            """);
        }
    }

    @Test
    void testContainerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldFindEventTypes() {
        List<String> types = repository.findEventTypes();
        assertNotNull(types);
    }

    @Test
    void shouldFindTimeseriesMetrics() {
        List<TimeseriesMetricRow> result = repository.findTimeseriesMetrics(Optional.of("click"));

        assertNotNull(result);
        assertEquals("click", result.get(0).normalizedTag());
        assertEquals(100, result.get(0).impressions());
        assertEquals(50, result.get(0).clicks());
        assertEquals(50.0, result.get(0).ctr());
    }

    @Test
    void shouldFindSiteMetrics() {
        List<SiteMetricRow> result = repository.findSiteMetrics(Optional.of("click"));

        assertNotNull(result);
        assertEquals("test.com", result.get(0).siteId());
        assertEquals(100, result.get(0).impressions());
    }

    @Test
    void shouldFindDmaMetrics() {
        List<DmaMetricRow> result = repository.findDmaMetrics(Optional.of("registration"));

        assertNotNull(result);
        assertEquals(501, result.get(0).mmDma());
        assertEquals(100, result.get(0).impressions());
    }

    @Test
    void shouldReturnEmptyListForUnknownTag() {
        List<TimeseriesMetricRow> result = repository.findTimeseriesMetrics(Optional.of("unknown"));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}