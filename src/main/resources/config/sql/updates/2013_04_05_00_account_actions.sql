RENAME TABLE `user_actions` TO `account_actions`;

ALTER TABLE `account_actions` CHANGE `user_id` `account_id` BIGINT(20)  UNSIGNED  NOT NULL  DEFAULT '0';

UPDATE account_actions x SET account_id = (SELECT id from ACCOUNTS WHERE user_id = x.account_id);

UPDATE account_actions x SET action_to_id = (SELECT id from ACCOUNTS WHERE user_id = x.action_to_id) WHERE action_to_id IS NOT NULL;

UPDATE account_actions x SET action_on = 'account', action_on_id = (SELECT id from ACCOUNTS WHERE user_id = action_on_id) WHERE action_on = 'user';
