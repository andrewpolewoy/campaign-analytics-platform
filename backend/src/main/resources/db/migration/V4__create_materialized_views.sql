DROP TABLE IF EXISTS campaign_metrics_timeseries;
DROP TABLE IF EXISTS campaign_metrics_site;
DROP TABLE IF EXISTS campaign_metrics_dma;

CREATE MATERIALIZED VIEW mv_metrics_timeseries AS
SELECT tag.metric_time,
       tag.normalized_tag,
       totals.impressions,
       totals.clicks,
       tag.events,
       100.0 * totals.clicks / NULLIF(totals.impressions, 0) AS ctr,
       1000.0 * tag.events / NULLIF(totals.impressions, 0) AS evpm
FROM (
    SELECT date_trunc('day', i.reg_time)::timestamp AS metric_time,
           ie.normalized_tag,
           COUNT(*) AS events
    FROM impression i
    INNER JOIN impression_event ie ON ie.uid = i.uid
    GROUP BY date_trunc('day', i.reg_time)::timestamp, ie.normalized_tag
) tag
INNER JOIN (
    SELECT date_trunc('day', i.reg_time)::timestamp AS metric_time,
           COUNT(DISTINCT i.uid) AS impressions,
           COUNT(DISTINCT CASE WHEN ie.raw_tag = 'fclick' THEN i.uid END) AS clicks
    FROM impression i
    INNER JOIN impression_event ie ON ie.uid = i.uid
    GROUP BY date_trunc('day', i.reg_time)::timestamp
) totals ON totals.metric_time = tag.metric_time;

CREATE UNIQUE INDEX CONCURRENTLY idx_mv_metrics_timeseries_grain
    ON mv_metrics_timeseries (metric_time, normalized_tag);

CREATE MATERIALIZED VIEW mv_metrics_site AS
SELECT tag.site_id,
       tag.normalized_tag,
       totals.impressions,
       totals.clicks,
       tag.events,
       100.0 * totals.clicks / NULLIF(totals.impressions, 0) AS ctr,
       1000.0 * tag.events / NULLIF(totals.impressions, 0) AS evpm
FROM (
    SELECT i.site_id,
           ie.normalized_tag,
           COUNT(*) AS events
    FROM impression i
    INNER JOIN impression_event ie ON ie.uid = i.uid
    WHERE i.site_id IS NOT NULL
    GROUP BY i.site_id, ie.normalized_tag
) tag
INNER JOIN (
    SELECT i.site_id,
           COUNT(DISTINCT i.uid) AS impressions,
           COUNT(DISTINCT CASE WHEN ie.raw_tag = 'fclick' THEN i.uid END) AS clicks
    FROM impression i
    INNER JOIN impression_event ie ON ie.uid = i.uid
    WHERE i.site_id IS NOT NULL
    GROUP BY i.site_id
) totals ON totals.site_id = tag.site_id;

CREATE UNIQUE INDEX idx_mv_metrics_site_grain
    ON mv_metrics_site (site_id, normalized_tag);

CREATE MATERIALIZED VIEW mv_metrics_dma AS
SELECT tag.mm_dma,
       tag.normalized_tag,
       totals.impressions,
       totals.clicks,
       tag.events,
       100.0 * totals.clicks / NULLIF(totals.impressions, 0) AS ctr,
       1000.0 * tag.events / NULLIF(totals.impressions, 0) AS evpm
FROM (
    SELECT i.mm_dma,
           ie.normalized_tag,
           COUNT(*) AS events
    FROM impression i
    INNER JOIN impression_event ie ON ie.uid = i.uid
    WHERE i.mm_dma IS NOT NULL
    GROUP BY i.mm_dma, ie.normalized_tag
) tag
INNER JOIN (
    SELECT i.mm_dma,
           COUNT(DISTINCT i.uid) AS impressions,
           COUNT(DISTINCT CASE WHEN ie.raw_tag = 'fclick' THEN i.uid END) AS clicks
    FROM impression i
    INNER JOIN impression_event ie ON ie.uid = i.uid
    WHERE i.mm_dma IS NOT NULL
    GROUP BY i.mm_dma
) totals ON totals.mm_dma = tag.mm_dma;

CREATE UNIQUE INDEX idx_mv_metrics_dma_grain
    ON mv_metrics_dma (mm_dma, normalized_tag);
