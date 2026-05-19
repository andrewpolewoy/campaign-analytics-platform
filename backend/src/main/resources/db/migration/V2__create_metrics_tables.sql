CREATE TABLE campaign_metrics_timeseries (
    metric_time  TIMESTAMP         NOT NULL,
    tag          VARCHAR(64)       NOT NULL,
    impressions  BIGINT            NOT NULL,
    clicks       BIGINT            NOT NULL,
    events       BIGINT            NOT NULL,
    ctr          DOUBLE PRECISION  NOT NULL,
    evpm         DOUBLE PRECISION  NOT NULL,
    PRIMARY KEY (metric_time, tag)
);

CREATE INDEX idx_campaign_metrics_timeseries_tag ON campaign_metrics_timeseries (tag);
CREATE INDEX idx_campaign_metrics_timeseries_metric_time ON campaign_metrics_timeseries (metric_time);

CREATE TABLE campaign_metrics_site (
    site_id      VARCHAR(255)      NOT NULL,
    tag          VARCHAR(64)       NOT NULL,
    impressions  BIGINT            NOT NULL,
    clicks       BIGINT            NOT NULL,
    events       BIGINT            NOT NULL,
    ctr          DOUBLE PRECISION  NOT NULL,
    evpm         DOUBLE PRECISION  NOT NULL,
    PRIMARY KEY (site_id, tag)
);

CREATE INDEX idx_campaign_metrics_site_tag ON campaign_metrics_site (tag);
CREATE INDEX idx_campaign_metrics_site_site_id ON campaign_metrics_site (site_id);

CREATE TABLE campaign_metrics_dma (
    mm_dma       INTEGER           NOT NULL,
    tag          VARCHAR(64)       NOT NULL,
    impressions  BIGINT            NOT NULL,
    clicks       BIGINT            NOT NULL,
    events       BIGINT            NOT NULL,
    ctr          DOUBLE PRECISION  NOT NULL,
    evpm         DOUBLE PRECISION  NOT NULL,
    PRIMARY KEY (mm_dma, tag)
);

CREATE INDEX idx_campaign_metrics_dma_tag ON campaign_metrics_dma (tag);
CREATE INDEX idx_campaign_metrics_dma_mm_dma ON campaign_metrics_dma (mm_dma);
