CREATE OR REPLACE VIEW v_event_normalized AS
SELECT
    uid,
    tag AS raw_tag,
    CASE tag
        WHEN 'vregistration' THEN 'registration'
        WHEN 'vpurchase' THEN 'purchase'
        WHEN 'vinstall' THEN 'install'
        WHEN 'vcontent' THEN 'content'
        WHEN 'fclick' THEN 'click'
        ELSE tag
    END AS normalized_tag
FROM impression_event;