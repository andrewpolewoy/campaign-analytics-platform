package com.campaign.analytics.dto;

import java.time.LocalDateTime;

public record TimeseriesMetricRow(
        LocalDateTime metricTime,
        String normalizedTag,
        long impressions,
        long clicks,
        long events,
        double ctr,
        double evpm
) {
}
