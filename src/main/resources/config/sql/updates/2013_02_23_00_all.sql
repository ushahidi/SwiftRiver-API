ALTER TABLE `user_followers` RENAME TO `account_followers`;
ALTER TABLE `account_followers` CHANGE `user_id` `account_id` BIGINT(20)  UNSIGNED  NOT NULL  DEFAULT '0';

ALTER TABLE `accounts` ADD `version` bigint(20) unsigned DEFAULT '1';

ALTER TABLE `bucket_collaborators` CHANGE `user_id` `account_id` BIGINT(11)  UNSIGNED  NOT NULL  DEFAULT '0';
ALTER TABLE `bucket_collaborators` ADD `date_added` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';
ALTER TABLE `bucket_collaborators` CHANGE `collaborator_active` `collaborator_active` TINYINT(1)  NULL  DEFAULT '0';
ALTER TABLE `bucket_collaborators` CHANGE `read_only` `read_only` TINYINT(1)  NULL  DEFAULT '1';


ALTER TABLE `bucket_subscriptions` RENAME TO `bucket_followers`;
ALTER TABLE `bucket_followers` CHANGE `user_id` `account_id` BIGINT(20)  NOT NULL;

ALTER TABLE `buckets` CHANGE `id` `id` BIGINT(11)  UNSIGNED  NOT NULL  AUTO_INCREMENT;
ALTER TABLE `buckets` CHANGE `account_id` `account_id` BIGINT(11)  UNSIGNED  NOT NULL  DEFAULT '0';
ALTER TABLE `buckets` DROP `user_id`;
ALTER TABLE `buckets` ADD `drop_count` int(11) DEFAULT '0';

ALTER TABLE `channel_filters` RENAME TO `river_channels`;
ALTER TABLE `river_channels` DROP `user_id`;
ALTER TABLE `river_channels` DROP `filter_name`;
ALTER TABLE `river_channels` DROP `filter_description`;
ALTER TABLE `river_channels` CHANGE `filter_enabled` `active` TINYINT(4)  NOT NULL  DEFAULT '1';
ALTER TABLE `river_channels` CHANGE `filter_date_add` `date_added` TIMESTAMP  NOT NULL  DEFAULT '0000-00-00 00:00:00';
ALTER TABLE `river_channels` CHANGE `filter_date_modified` `date_modified` TIMESTAMP  NOT NULL  DEFAULT '0000-00-00 00:00:00';
ALTER TABLE `river_channels` DROP `filter_last_run`;
ALTER TABLE `river_channels` DROP `filter_last_successful_run`;
ALTER TABLE `river_channels` DROP `filter_runs`;
ALTER TABLE `river_channels` ADD `drop_count` int(11) NOT NULL DEFAULT '0';

ALTER TABLE `river_collaborators` CHANGE `user_id` `account_id` BIGINT(20)  NULL  DEFAULT NULL;
ALTER TABLE `river_collaborators` ADD `date_added` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';
ALTER TABLE `river_collaborators` CHANGE `read_only` `read_only` TINYINT(1)  NULL  DEFAULT '1';

ALTER TABLE `river_subscriptions` RENAME TO `river_followers`;
ALTER TABLE `river_followers` CHANGE `user_id` `account_id` BIGINT(20)  NOT NULL;

ALTER TABLE `rivers` CHANGE `river_name_url` `river_name_canonical` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT '';
ALTER TABLE `rivers` ADD `description` varchar(255) DEFAULT NULL;

ALTER TABLE `rivers_droplets` ADD `drop_count` int(11) NOT NULL DEFAULT '0';
ALTER TABLE `rivers_droplets` ADD `channel_id` bigint(20) unsigned DEFAULT '0';
ALTER TABLE `rivers_droplets` ADD KEY `channel_id` (`channel_id`);
