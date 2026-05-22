package com.campaign.analytics.repository;

import com.campaign.analytics.dto.DmaMetricRow;
import com.campaign.analytics.dto.SiteMetricRow;
import com.campaign.analytics.dto.TimeseriesMetricRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MetricsRepository {

    private static final String SELECT_TIMESERIES = """
            SELECT metric_time, normalized_tag, impressions, clicks, events, ctr, evpm
            FROM mv_timeseries
            WHERE ? IS NULL OR normalized_tag = ?
            ORDER BY metric_time, normalized_tag
            """;

    private static final String SELECT_SITE = """
            SELECT site_id, normalized_tag, impressions, clicks, events, ctr, evpm
            FROM mv_site
            WHERE ? IS NULL OR normalized_tag = ?
            ORDER BY site_id, normalized_tag
            """;

    private static final String SELECT_DMA = """
            SELECT mm_dma, normalized_tag, impressions, clicks, events, ctr, evpm
            FROM mv_dma
            WHERE ? IS NULL OR normalized_tag = ?
            ORDER BY mm_dma, normalized_tag
            """;

    private static final String SELECT_EVENT_TYPES = """
            SELECT DISTINCT normalized_tag
            FROM impression_event
            ORDER BY normalized_tag
            """;

    @Inject
    private DataSource dataSource;

    public List<String> findEventTypes() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_EVENT_TYPES);
             ResultSet resultSet = statement.executeQuery()) {
            List<String> eventTypes = new ArrayList<>();
            while (resultSet.next()) {
                eventTypes.add(resultSet.getString("normalized_tag"));
            }
            return eventTypes;
        } catch (SQLException exception) {
            throw new MetricsQueryException("Failed to load event types", exception);
        }
    }

    public List<TimeseriesMetricRow> findTimeseriesMetrics(Optional<String> normalizedTag) {
        String tag = normalizedTag.orElse(null);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_TIMESERIES)) {
            statement.setString(1, tag);
            statement.setString(2, tag);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<TimeseriesMetricRow> rows = new ArrayList<>();
                while (resultSet.next()) {
                    rows.add(mapTimeseriesRow(resultSet));
                }
                return rows;
            }
        } catch (SQLException exception) {
            throw new MetricsQueryException("Failed to load timeseries metrics", exception);
        }
    }

    public List<SiteMetricRow> findSiteMetrics(Optional<String> normalizedTag) {
        String tag = normalizedTag.orElse(null);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SITE)) {
            statement.setString(1, tag);
            statement.setString(2, tag);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<SiteMetricRow> rows = new ArrayList<>();
                while (resultSet.next()) {
                    rows.add(mapSiteRow(resultSet));
                }
                return rows;
            }
        } catch (SQLException exception) {
            throw new MetricsQueryException("Failed to load site metrics", exception);
        }
    }

    public List<DmaMetricRow> findDmaMetrics(Optional<String> normalizedTag) {
        String tag = normalizedTag.orElse(null);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_DMA)) {
            statement.setString(1, tag);
            statement.setString(2, tag);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<DmaMetricRow> rows = new ArrayList<>();
                while (resultSet.next()) {
                    rows.add(mapDmaRow(resultSet));
                }
                return rows;
            }
        } catch (SQLException exception) {
            throw new MetricsQueryException("Failed to load DMA metrics", exception);
        }
    }

    public void refreshMaterializedViews() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT refresh_metrics_views()")) {
            stmt.execute();
        } catch (SQLException e) {
            throw new MetricsQueryException("Failed to refresh materialized views", e);
        }
    }

    private static TimeseriesMetricRow mapTimeseriesRow(ResultSet resultSet) throws SQLException {
        Timestamp metricTime = resultSet.getTimestamp("metric_time");
        return new TimeseriesMetricRow(
                metricTime.toLocalDateTime(),
                resultSet.getString("normalized_tag"),
                resultSet.getLong("impressions"),
                resultSet.getLong("clicks"),
                resultSet.getLong("events"),
                resultSet.getDouble("ctr"),
                resultSet.getDouble("evpm")
        );
    }

    private static SiteMetricRow mapSiteRow(ResultSet resultSet) throws SQLException {
        return new SiteMetricRow(
                resultSet.getString("site_id"),
                resultSet.getString("normalized_tag"),
                resultSet.getLong("impressions"),
                resultSet.getLong("clicks"),
                resultSet.getLong("events"),
                resultSet.getDouble("ctr"),
                resultSet.getDouble("evpm")
        );
    }

    private static DmaMetricRow mapDmaRow(ResultSet resultSet) throws SQLException {
        return new DmaMetricRow(
                resultSet.getInt("mm_dma"),
                resultSet.getString("normalized_tag"),
                resultSet.getLong("impressions"),
                resultSet.getLong("clicks"),
                resultSet.getLong("events"),
                resultSet.getDouble("ctr"),
                resultSet.getDouble("evpm")
        );
    }
}
