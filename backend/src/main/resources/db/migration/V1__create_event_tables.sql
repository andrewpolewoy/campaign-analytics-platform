CREATE TABLE event (
    id           BIGSERIAL PRIMARY KEY,
    uid          UUID         NOT NULL UNIQUE,
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

CREATE INDEX idx_event_site_id ON event (site_id);
CREATE INDEX idx_event_mm_dma ON event (mm_dma);
CREATE INDEX idx_event_reg_time ON event (reg_time);

CREATE TABLE event_tag (
    id         BIGSERIAL PRIMARY KEY,
    event_uid  UUID        NOT NULL,
    tag        VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_tag_event_uid
        FOREIGN KEY (event_uid) REFERENCES event (uid) ON DELETE CASCADE
);

CREATE INDEX idx_event_tag_event_uid ON event_tag (event_uid);
CREATE INDEX idx_event_tag_tag ON event_tag (tag);
