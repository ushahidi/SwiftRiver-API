ALTER TABLE `buckets` CHANGE `id` `id` BIGINT(11)  UNSIGNED  NOT NULL  AUTO_INCREMENT;
ALTER TABLE `buckets` CHANGE `account_id` `account_id` BIGINT(11)  UNSIGNED  NOT NULL  DEFAULT '0';
ALTER TABLE `buckets` DROP `user_id`;
ALTER TABLE `buckets` ADD `drop_count` int(11) DEFAULT '0';
ALTER TABLE `buckets` CHANGE `bucket_name_url` `bucket_name_canonical` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT '';
ALTER TABLE `buckets` CHANGE `bucket_publish` `bucket_publish` TINYINT(1)  NOT NULL  DEFAULT '0';
ALTER TABLE `buckets` ADD UNIQUE INDEX (`account_id`, `bucket_name_canonical`);

-- `bucket_collaborators`
ALTER TABLE `bucket_collaborators` CHANGE `user_id` `account_id` BIGINT(11)  UNSIGNED  NOT NULL  DEFAULT '0';
ALTER TABLE `bucket_collaborators` ADD `date_added` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';
ALTER TABLE `bucket_collaborators` CHANGE `collaborator_active` `collaborator_active` TINYINT(1)  NULL  DEFAULT '0';
ALTER TABLE `bucket_collaborators` CHANGE `read_only` `read_only` TINYINT(1)  NULL  DEFAULT '1';

-- `bucket_followers`
ALTER TABLE `bucket_subscriptions` RENAME TO `bucket_followers`;
ALTER TABLE `bucket_followers` CHANGE `user_id` `account_id` BIGINT(20)  NOT NULL;
