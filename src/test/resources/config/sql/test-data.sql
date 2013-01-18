START TRANSACTION;

-- -----------------------------------------------------
-- Data for table `roles`
-- -----------------------------------------------------
TRUNCATE TABLE `roles`;
INSERT INTO `roles` (`id`, `name`, `description`, `permissions`) VALUES 
(1, 'login', 'Login privileges, granted after account confirmation', NULL),
(2, 'admin', 'Super Administrator', NULL);


-- -----------------------------------------------------
-- Data for table `settings`
-- -----------------------------------------------------
TRUNCATE TABLE `settings`;
INSERT INTO `settings` (`id`, `key`, `value`) VALUES 
(1, 'site_name', 'SwiftRiver'),
(2, 'site_theme', 'default'),
(3, 'site_locale', 'en'),
(4, 'public_registration_enabled', '0'),
(5, 'anonymous_access_enabled', '0'),
(6, 'default_river_lifetime', '14'),
(7, 'river_expiry_notice_period', '3'),
(8, 'general_invites_enabled', '0'),
(9, 'default_river_quota', '1'),
(10, 'default_river_drop_quota', '10000'),
(11, 'site_url', 'http://www.example.com'),
(12, 'email_domain', 'example.com'),
(13, 'comments_email_domain', 'example.com');

-- -----------------------------------------------------
-- Data for table `users`
-- -----------------------------------------------------
TRUNCATE TABLE `users`;
INSERT INTO `users` (`id`, `email`, `name`, `username`, `password`, `logins`, `last_login`, `api_key`) VALUES 
(1, 'myswiftriver@myswiftriver.com', 'Administrator', 'admin', 'c2bac288881c7dd9531c607e73b3af798499917760023656e9847b10b8e75542', 0, NULL, md5(rand())),
(2, 'public@myswiftriver.com', 'public', 'public', '', 0, NULL, '');

-- -----------------------------------------------------
-- Data for table `roles_users`
-- -----------------------------------------------------
TRUNCATE TABLE `roles_users`;
INSERT INTO `roles_users` (`user_id`, `role_id`, `account_id`) VALUES 
(1, 1, 1),
(1, 2, 1);

-- -----------------------------------------------------
-- Data for table `accounts`
-- -----------------------------------------------------
TRUNCATE TABLE `accounts`;
INSERT INTO `accounts` (`user_id`, `account_path`) VALUES 
(1, 'default'),
(2, 'public');

-- -----------------------------------------------------
-- Data for table `plugins`
-- -----------------------------------------------------
TRUNCATE TABLE `plugins`;
INSERT INTO `plugins` (`id`, `plugin_path`, `plugin_name`, `plugin_description`, `plugin_enabled`, `plugin_weight`, `plugin_installed`)
VALUES
	(1,'rss','RSS','Adds an RSS/Atom channel to SwiftRiver to parse RSS and Atom Feeds.',1,1,0),
	(2,'twitter','Twitter','Adds a Twitter channel to SwiftRiver.',1,1,0);

COMMIT;