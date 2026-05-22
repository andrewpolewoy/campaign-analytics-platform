CREATE TABLE impression (
    id           BIGSERIAL PRIMARY KEY,
    uid          UUID         NOT NULL,
    reg_time     TIMESTAMPTZ    NOT NULL,
    fc_imp_chk   INTEGER,
    fc_time_chk  INTEGER,
    utmtr        INTEGER,
    mm_dma       INTEGER,
    os_name      VARCHAR(64),
    model        VARCHAR(128),
    hardware     VARCHAR(64),
    site_id      VARCHAR(255),
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_event_site_id ON impression (site_id);
CREATE INDEX idx_event_mm_dma ON impression (mm_dma);
CREATE INDEX idx_event_reg_time ON impression (reg_time);

CREATE TABLE impression_event (
    id BIGSERIAL PRIMARY KEY,
    uid UUID NOT NULL,
    tag VARCHAR(255) NOT NULL,
    normalized_tag VARCHAR(255)
);

CREATE INDEX idx_impression_uid
    ON impression(uid);

CREATE INDEX idx_event_uid
    ON impression_event(uid);

CREATE INDEX idx_event_normalized_tag
    ON impression_event(normalized_tag);