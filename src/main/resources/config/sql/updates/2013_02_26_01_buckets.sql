ALTER TABLE `buckets` CHANGE `bucket_name_url` `bucket_name_canonical` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT '';
ALTER TABLE `buckets` CHANGE `bucket_publish` `bucket_publish` TINYINT(1)  NOT NULL  DEFAULT '0';
ALTER TABLE `buckets` ADD UNIQUE INDEX (`account_id`, `bucket_name_canonical`);