ALTER TABLE `rivers` CHANGE `river_active` `river_active` TINYINT(4)  NULL  DEFAULT '1';
ALTER TABLE `rivers` CHANGE `drop_count` `drop_count` INT(11)  NULL  DEFAULT '0';
ALTER TABLE `rivers` CHANGE `river_expired` `river_expired` TINYINT(1)  NULL  DEFAULT '0'  COMMENT 'Whether the river has expired';
ALTER TABLE `rivers` CHANGE `expiry_notification_sent` `expiry_notification_sent` TINYINT(1)  NULL  DEFAULT '0'  COMMENT 'Flags whether the river has been marked for expiry';
ALTER TABLE `rivers` CHANGE `extension_count` `extension_count` INT(11)  NULL  DEFAULT '0'  COMMENT 'The no. of times the expiry date has been extended';
ALTER TABLE `rivers` CHANGE `river_full` `river_full` TINYINT(1)  NULL  DEFAULT '0'  COMMENT 'Whether the river has expired';
ALTER TABLE `rivers` CHANGE `max_drop_id` `max_drop_id` BIGINT(20)  NULL  DEFAULT '0';
