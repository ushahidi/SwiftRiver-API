START TRANSACTION;

-- -----------------------------------------------------
-- Date are at UTC
-- -----------------------------------------------------
SET time_zone = "+00:00";

-- -----------------------------------------------------
-- Data for table `roles`
-- -----------------------------------------------------
INSERT INTO `roles` (`id`, `name`, `description`, `permissions`) VALUES 
(1, 'login', 'Login privileges, granted after account confirmation', NULL),
(2, 'admin', 'Super Administrator', NULL);


-- -----------------------------------------------------
-- Data for table `settings`
-- -----------------------------------------------------
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
INSERT INTO `users` (`id`, `email`, `name`, `username`, `password`, `logins`, `last_login`, `api_key`, created_date) VALUES 
(1, 'myswiftriver@myswiftriver.com', 'Administrator', 'admin', 'c2bac288881c7dd9531c607e73b3af798499917760023656e9847b10b8e75542', 0, NULL, md5(rand()), '2013-01-01 00:00:00'),
(2, 'public@myswiftriver.com', 'public', 'public', '', 0, NULL, '', '2013-01-01 00:00:01'),
(3, 'user1@myswiftriver.com', 'User 1', 'user1', 'user1_password', 0, NULL, 'user1', '2013-01-01 00:00:02'),
(4, 'user2@myswiftriver.com', 'User 2', 'user2', 'user2_password', 0, NULL, 'user2', '2013-01-01 00:00:03'),
(5, 'user3@myswiftriver.com', 'User 3', 'user3', 'user3_password', 0, NULL, 'user3', '2013-01-01 00:00:04');

-- -----------------------------------------------------
-- Data for table `roles_users`
-- -----------------------------------------------------
INSERT INTO `roles_users` (`user_id`, `role_id`, `account_id`) VALUES 
(1, 1, 1),
(1, 2, 1);

-- -----------------------------------------------------
-- Data for table `accounts`
-- -----------------------------------------------------
INSERT INTO `accounts` (`user_id`, `account_path`, `account_private`, `account_date_add`, `account_date_modified`, `account_active`, `river_quota_remaining`) VALUES 
(1, 'default', 0, '2013-01-01 00:00:00', '2013-01-02 00:00:00', 1, 10),
(2, 'public', 0, '2013-01-01 00:00:01', '2013-01-02 00:00:01', 1, 15),
(3, 'user1', 0, '2013-01-01 00:00:02', '2013-01-02 00:00:02', 1, 20),
(4, 'user2', 0, '2013-01-01 00:00:03', '2013-01-02 00:00:03', 1, 25),
(5, 'user3', 0, '2013-01-01 00:00:04', '2013-01-02 00:00:04', 1, 30);

-- -----------------------------------------------------
-- Data for table `account_followers`
-- -----------------------------------------------------
INSERT INTO `account_followers` (`account_id`, `follower_id`, `follower_date_add`) VALUES
(3, 4, '2013-01-02 00:00:03'),
(3, 5, '2013-01-02 00:00:04'),
(5, 3, '2013-01-02 00:00:02');

-- -----------------------------------------------------
-- Data for table `plugins`
-- -----------------------------------------------------
INSERT INTO `plugins` (`id`, `plugin_path`, `plugin_name`, `plugin_description`, `plugin_enabled`, `plugin_weight`, `plugin_installed`)
VALUES
	(1,'rss','RSS','Adds an RSS/Atom channel to SwiftRiver to parse RSS and Atom Feeds.',1,1,0),
	(2,'twitter','Twitter','Adds a Twitter channel to SwiftRiver.',1,1,0);
	
-- -----------------------------------------------------
-- Data for table `rivers`
-- -----------------------------------------------------
INSERT INTO `rivers` (`id`, `account_id`, `river_name`, `river_active`, `river_public`, `drop_count`, `drop_quota`, `river_full`, `river_date_add`, `river_date_expiry`, `river_expired`, `extension_count`) VALUES 
(1, 3, 'River 1', 1, 1, 100, 10000, 0, '2013-01-02 00:00:02', '2013-02-02 00:00:02', 0, 0);

-- -----------------------------------------------------
-- Data for table `buckets`
-- -----------------------------------------------------
INSERT INTO `buckets` (`id`, `account_id`, `bucket_name`, `bucket_description`, `bucket_publish`, `bucket_date_add`, `drop_count`) VALUES 
(1, 3, 'Bucket 1', 'A Bucket', 1, '2013-01-02 00:00:02', 13);


COMMIT;