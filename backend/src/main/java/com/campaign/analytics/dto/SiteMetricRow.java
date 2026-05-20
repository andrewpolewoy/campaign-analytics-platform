package com.campaign.analytics.dto;

public record SiteMetricRow(
        String siteId,
        String normalizedTag,
        long impressions,
        long clicks,
        long events,
        double ctr,
        double evpm
) {
}
