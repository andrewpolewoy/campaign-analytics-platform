CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_timeseries
    ON mv_timeseries(metric_time, normalized_tag);

CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_site
    ON mv_site(site_id, normalized_tag);

CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_dma
    ON mv_dma(mm_dma, normalized_tag);