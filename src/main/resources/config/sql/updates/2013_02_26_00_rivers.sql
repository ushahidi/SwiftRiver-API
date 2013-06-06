ALTER TABLE `rivers` CHANGE `river_active` `river_active` TINYINT(4)  NULL  DEFAULT '1';
ALTER TABLE `rivers` CHANGE `drop_count` `drop_count` INT(11)  NULL  DEFAULT '0';
ALTER TABLE `rivers` CHANGE `river_expired` `river_expired` TINYINT(1)  NULL  DEFAULT '0'  COMMENT 'Whether the river has expired';
ALTER TABLE `rivers` CHANGE `expiry_notification_sent` `expiry_notification_sent` TINYINT(1)  NULL  DEFAULT '0'  COMMENT 'Flags whether the river has been marked for expiry';
ALTER TABLE `rivers` CHANGE `extension_count` `extension_count` INT(11)  NULL  DEFAULT '0'  COMMENT 'The no. of times the expiry date has been extended';
ALTER TABLE `rivers` CHANGE `river_full` `river_full` TINYINT(1)  NULL  DEFAULT '0'  COMMENT 'Whether the river has expired';
ALTER TABLE `rivers` CHANGE `max_drop_id` `max_drop_id` BIGINT(20)  NULL  DEFAULT '0';
ALTER TABLE `rivers` CHANGE `river_name_url` `river_name_canonical` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT '';
ALTER TABLE `rivers` ADD `description` varchar(255) DEFAULT NULL;

-- `river_collaborators`
ALTER TABLE `river_collaborators` CHANGE `user_id` `account_id` BIGINT(20)  NULL  DEFAULT NULL;
ALTER TABLE `river_collaborators` ADD `date_added` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';
ALTER TABLE `river_collaborators` CHANGE `read_only` `read_only` TINYINT(1)  NULL  DEFAULT '1';

-- `river_followers`
ALTER TABLE `river_subscriptions` RENAME TO `river_followers`;
ALTER TABLE `river_followers` CHANGE `user_id` `account_id` BIGINT(20)  NOT NULL;