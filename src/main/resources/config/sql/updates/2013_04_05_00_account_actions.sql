RENAME TABLE `user_actions` TO `account_actions`;
ALTER TABLE `account_actions` CHANGE `user_id` `account_id` BIGINT(20)  UNSIGNED  NOT NULL  DEFAULT '0';

UPDATE `account_actions` x JOIN (SELECT `id`, `user_id` from `accounts`) AS `t`
SET `x`.`account_id` = t.id
WHERE `t`.`user_id` = `x`.`account_id`;

UPDATE `account_actions` `x` JOIN (SELECT `id`, `user_id` from `accounts`) AS `t`
SET `x`.`action_to_id` = `t`.`id`
WHERE `x`.`action_to_id` = `t`.`user_id`
AND `x`.`action_to_id` IS NOT NULL;


UPDATE `account_actions` `x` JOIN (SELECT `id`, `user_id` from `accounts`) AS `t`
SET `x`.`action_on` = 'account', `action_on_id` = `t`.`id`
WHERE `x`.action_on_id = `t`.`user_id`
AND `x`.`action_on` = 'user';
