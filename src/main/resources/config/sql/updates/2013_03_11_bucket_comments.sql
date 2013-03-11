ALTER TABLE `bucket_comments` CHANGE `id` `id` BIGINT(20)  UNSIGNED  NOT NULL  AUTO_INCREMENT;
ALTER TABLE `bucket_comments` CHANGE `bucket_id` `bucket_id` BIGINT(20)  UNSIGNED  NOT NULL  DEFAULT '0';
ALTER TABLE `bucket_comments` CHANGE `user_id` `account_id` BIGINT(20)  UNSIGNED  NOT NULL  DEFAULT '0';
ALTER TABLE `bucket_comments` DROP `parent_id`;
ALTER TABLE `bucket_comments` CHANGE `comment_content` `comment_text` TEXT  NOT NULL;
ALTER TABLE `bucket_comments` DROP `comment_date_modified`;
ALTER TABLE `bucket_comments` DROP `comment_sticky`;
ALTER TABLE `bucket_comments` DROP `comment_deleted`;
ALTER TABLE `bucket_comments` ADD INDEX `idx_bucket_account_id` (`bucket_id`, `account_id`);
