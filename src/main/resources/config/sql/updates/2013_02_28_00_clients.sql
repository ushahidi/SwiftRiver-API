ALTER TABLE `clients` ADD `account_id` BIGINT(11)  NOT NULL;
ALTER TABLE `clients` CHANGE `active` `active` TINYINT(1)  NULL  DEFAULT '1';

