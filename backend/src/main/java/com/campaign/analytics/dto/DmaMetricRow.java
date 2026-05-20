package com.campaign.analytics.dto;

public record DmaMetricRow(
        int mmDma,
        String normalizedTag,
        long impressions,
        long clicks,
        long events,
        double ctr,
        double evpm
) {
}
