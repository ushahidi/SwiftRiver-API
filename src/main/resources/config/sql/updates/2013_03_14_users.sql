ALTER TABLE `users` ADD `last_login_timestamp` TIMESTAMP  NOT NULL  DEFAULT '0000-00-00 00:00:00';
UPDATE `users` SET `last_login_timestamp` = FROM_UNIXTIME(`last_login`) WHERE `last_login` IS NOT NULL;
ALTER TABLE `users` CHANGE `last_login` `last_login` TIMESTAMP  NOT NULL  DEFAULT '0000-00-00 00:00:00';
UPDATE `users` SET `last_login` = `last_login_timestamp` WHERE `last_login_timestamp` <> '0000-00-00 00:00:00';
ALTER TABLE `users` DROP `last_login_timestamp`;