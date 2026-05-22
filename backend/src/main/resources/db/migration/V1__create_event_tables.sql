CREATE TABLE impression (
    id BIGSERIAL PRIMARY KEY,
    uid UUID NOT NULL UNIQUE,
    reg_time TIMESTAMP NOT NULL,
    site_id VARCHAR(255),
    mm_dma INTEGER);

CREATE TABLE impression_event (
    id BIGSERIAL PRIMARY KEY,
    uid UUID NOT NULL,
    reg_time TIMESTAMP NOT NULL,
    tag VARCHAR(255) NOT NULL,
    normalized_tag VARCHAR(255)
);

CREATE INDEX idx_impression_uid
    ON impression(uid);

CREATE INDEX idx_event_uid
    ON impression_event(uid);

CREATE INDEX idx_event_normalized_tag
    ON impression_event(normalized_tag);