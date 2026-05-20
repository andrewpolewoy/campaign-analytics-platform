ALTER TABLE impression_event
    ADD COLUMN raw_tag VARCHAR(100),
    ADD COLUMN normalized_tag VARCHAR(100);

UPDATE impression_event
SET raw_tag = tag,
    normalized_tag = CASE tag
        WHEN 'vregistration' THEN 'registration'
        WHEN 'vpurchase' THEN 'purchase'
        WHEN 'vinstall' THEN 'install'
        WHEN 'vcontent' THEN 'content'
        ELSE tag
    END;

ALTER TABLE impression_event
    ALTER COLUMN raw_tag SET NOT NULL,
    ALTER COLUMN normalized_tag SET NOT NULL;

ALTER TABLE impression_event DROP COLUMN tag;

DROP INDEX idx_impression_event_tag;

CREATE INDEX idx_impression_event_normalized_tag ON impression_event (normalized_tag);
