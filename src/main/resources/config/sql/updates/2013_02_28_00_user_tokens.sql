ALTER TABLE `user_tokens` CHANGE `id` `id` BIGINT(11)  UNSIGNED  NOT NULL  AUTO_INCREMENT;
ALTER TABLE `user_tokens` CHANGE `user_id` `user_id` BIGINT(11)  UNSIGNED  NOT NULL;
ALTER TABLE `user_tokens` DROP `user_agent`;
ALTER TABLE `user_tokens` CHANGE `created` `created` TIMESTAMP  NOT NULL;
ALTER TABLE `user_tokens` CHANGE `expires` `expires` TIMESTAMP  NOT NULL;
