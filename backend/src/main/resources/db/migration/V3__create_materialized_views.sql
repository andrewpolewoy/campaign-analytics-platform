CREATE MATERIALIZED VIEW mv_timeseries AS
SELECT
    date_trunc('day', i.reg_time)::timestamp AS metric_time,
    e.normalized_tag,

    COUNT(DISTINCT i.uid) AS impressions,

    COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END) AS clicks,

    COUNT(*) AS events,

    100.0 * COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END)
        / NULLIF(COUNT(DISTINCT i.uid), 0) AS ctr,

    1000.0 * COUNT(*)
        / NULLIF(COUNT(DISTINCT i.uid), 0) AS evpm

FROM impression i
JOIN v_event_normalized e ON e.uid = i.uid
GROUP BY 1, 2
WITH DATA;


CREATE UNIQUE INDEX idx_mv_timeseries
ON mv_timeseries(metric_time, normalized_tag);



CREATE MATERIALIZED VIEW mv_site AS
SELECT
    i.site_id,
    e.normalized_tag,

    COUNT(DISTINCT i.uid) AS impressions,

    COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END) AS clicks,

    100.0 * COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END)
        / NULLIF(COUNT(DISTINCT i.uid), 0) AS ctr,

    1000.0 * COUNT(*)
        / NULLIF(COUNT(DISTINCT i.uid), 0) AS evpm

FROM impression i
JOIN v_event_normalized e ON e.uid = i.uid
WHERE i.site_id IS NOT NULL
GROUP BY i.site_id, e.normalized_tag
WITH DATA;


CREATE UNIQUE INDEX idx_mv_site
ON mv_site(site_id, normalized_tag);



CREATE MATERIALIZED VIEW mv_dma AS
SELECT
    i.mm_dma,
    e.normalized_tag,

    COUNT(DISTINCT i.uid) AS impressions,

    COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END) AS clicks,

    100.0 * COUNT(DISTINCT CASE WHEN e.normalized_tag = 'click' THEN i.uid END)
        / NULLIF(COUNT(DISTINCT i.uid), 0) AS ctr,

    1000.0 * COUNT(*)
        / NULLIF(COUNT(DISTINCT i.uid), 0) AS evpm

FROM impression i
JOIN v_event_normalized e ON e.uid = i.uid
WHERE i.mm_dma IS NOT NULL
GROUP BY i.mm_dma, e.normalized_tag
WITH DATA;


CREATE UNIQUE INDEX idx_mv_dma
ON mv_dma(mm_dma, normalized_tag);