-- delete date_of_check column in detection_history
ALTER TABLE detection_history DROP COLUMN date_of_check;
-- create date_of_check column in detection_history with bigint
ALTER TABLE detection_history ADD COLUMN date_of_check bigint NOT NULL;