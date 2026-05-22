DROP MATERIALIZED VIEW IF EXISTS mv_timeseries;

CREATE MATERIALIZED VIEW mv_timeseries AS
SELECT
    metric_time,
    normalized_tag,
    impressions,
    clicks,
    events,
    (100.0 * clicks / NULLIF(impressions, 0))::double precision AS ctr,
    (1000.0 * events / NULLIF(impressions, 0))::double precision AS evpm
FROM (
    SELECT
        date_trunc('day', i.reg_time)::timestamp AS metric_time,
        e.normalized_tag,
        COUNT(DISTINCT i.uid) AS impressions,
        COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END) AS clicks,
        COUNT(*) AS events
    FROM impression i
    JOIN v_event_normalized e ON e.uid = i.uid
    GROUP BY 1, 2
) sub;

DROP MATERIALIZED VIEW IF EXISTS mv_site;

CREATE MATERIALIZED VIEW mv_site AS
SELECT
    site_id,
    normalized_tag,
    impressions,
    clicks,
    events,
    (100.0 * clicks / NULLIF(impressions, 0))::double precision AS ctr,
    (1000.0 * events / NULLIF(impressions, 0))::double precision AS evpm
FROM (
    SELECT
        i.site_id,
        e.normalized_tag,
        COUNT(DISTINCT i.uid) AS impressions,
        COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END) AS clicks,
        COUNT(*) AS events
    FROM impression i
    JOIN v_event_normalized e ON e.uid = i.uid
    WHERE i.site_id IS NOT NULL
    GROUP BY i.site_id, e.normalized_tag
) sub;

DROP MATERIALIZED VIEW IF EXISTS mv_dma;

CREATE MATERIALIZED VIEW mv_dma AS
SELECT
    mm_dma,
    normalized_tag,
    impressions,
    clicks,
    events,
    (100.0 * clicks / NULLIF(impressions, 0))::double precision AS ctr,
    (1000.0 * events / NULLIF(impressions, 0))::double precision AS evpm
FROM (
    SELECT
        i.mm_dma,
        e.normalized_tag,
        COUNT(DISTINCT i.uid) AS impressions,
        COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END) AS clicks,
        COUNT(*) AS events
    FROM impression i
    JOIN v_event_normalized e ON e.uid = i.uid
    WHERE i.mm_dma IS NOT NULL
    GROUP BY i.mm_dma, e.normalized_tag
) sub;

COMMENT ON MATERIALIZED VIEW mv_timeseries IS 'Временной ряд: CTR и EvPM по дням и типам событий';
COMMENT ON MATERIALIZED VIEW mv_site IS 'Агрегация метрик по site_id';
COMMENT ON MATERIALIZED VIEW mv_dma IS 'Агрегация метрик по mm_dma';