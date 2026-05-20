ALTER TABLE event RENAME TO impression;

ALTER INDEX idx_event_site_id RENAME TO idx_impression_site_id;
ALTER INDEX idx_event_mm_dma RENAME TO idx_impression_mm_dma;
ALTER INDEX idx_event_reg_time RENAME TO idx_impression_reg_time;

ALTER TABLE event_tag RENAME TO impression_event;

ALTER TABLE impression_event RENAME COLUMN event_uid TO uid;

ALTER TABLE impression_event RENAME CONSTRAINT fk_event_tag_event_uid TO fk_impression_event_uid;

ALTER INDEX idx_event_tag_event_uid RENAME TO idx_impression_event_uid;
ALTER INDEX idx_event_tag_tag RENAME TO idx_impression_event_tag;
