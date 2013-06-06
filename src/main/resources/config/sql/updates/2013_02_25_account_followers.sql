ALTER TABLE `user_followers` RENAME TO `account_followers`;
ALTER TABLE `account_followers` CHANGE `user_id` `account_id` BIGINT(20)  UNSIGNED  NOT NULL  DEFAULT '0';

-- Add Primary Key column
ALTER TABLE `account_followers` ADD COLUMN `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;