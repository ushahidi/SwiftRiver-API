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
(2, 'public@myswiftriver.com', 'public', 'public', '', 0, NULL, ''),
(3, 'admin@example.com', 'Admin User 1', 'admin1', 'DCMFFIFK', 10, NULL, 'admin1'),
(4,'admin2@example.com', 'Admin User 2', 'admin2', 'FG$((ASH', 20, NULL, 'admin2'),
(5, 'admin3@example.com', 'Admin User 3', 'admin3', 'LKICOOUFN', 30, NULL, 'admin3'),
(6, 'admin4@example.com', 'Admin User 4', 'admin4', 'ZPODIFMANU', 40, NULL, 'admin4'),
(7, 'admin5@example.com', 'Admin User 5', 'admin5', 'TMNDHACFRDLA', 50, NULL, 'admin5');


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
(2, 'public'),
(3, 'admin1'),
(4, 'admin2'),
(5, 'admin3'),
(6, 'admin4'),
(7, 'admin5');


-- -----------------------------------------------------
-- Data for table `plugins`
-- -----------------------------------------------------
TRUNCATE TABLE `plugins`;
INSERT INTO `plugins` (`id`, `plugin_path`, `plugin_name`, `plugin_description`, `plugin_enabled`, `plugin_weight`, `plugin_installed`)
VALUES
	(1,'rss','RSS','Adds an RSS/Atom channel to SwiftRiver to parse RSS and Atom Feeds.',1,1,0),
	(2,'twitter','Twitter','Adds a Twitter channel to SwiftRiver.',1,1,0);

COMMIT;


-- -----------------------------------------------------
-- Data for table `seq`
-- -----------------------------------------------------
INSERT INTO `seq` (`name`, `id`) VALUES 
('droplets', 1),
('tags', 1),
('places', 1),
('links', 1),
('identities', 1),
('media', 1),
('river_tag_trends', 1),
('rivers_droplets', 1);


-- ------------------------
-- Data for table `rivers`
-- ------------------------
INSERT INTO rivers(id, account_id, river_name, river_name_url) VALUES
(1, 3, 'River Number One', 'river-number-one'),
(2, 4, 'River Number x', 'river-number-x');


-- ------------------------------------
-- Data for table `river_collaborators`
-- -------------------------------------
INSERT INTO river_collaborators(river_id, account_id, read_only) VALUES
(1, 2, 0),
(1, 5, 1);


-- --------------------------
-- Data for `channel_filters`
-- --------------------------
INSERT INTO `river_channels` (`id`, `channel`, `river_id`) VALUES
(1, 'rss', 1),
(2, 'email', 1),
(3, 'sms', 1);


-- ---------------------------------
-- Data for `channel_filter_options`
-- ---------------------------------
INSERT INTO `river_channel_options` (`id`, `river_channel_id`, `key`, `value`)
VALUES
	(1, 1, 'url', '{\"value\":\"http:\\/\\/feeds.boingboing.net\\/boingboing\\/iBag\",\"title\":\"Boing Boing\"}'),
	(2, 1, 'url', '{\"value\":\"http:\\/\\/feeds.feedburner.com\\/codinghorror\\/\",\"title\":\"Coding Horror\"}'),
	(3, 1, 'url', '{\"value\":\"http:\\/\\/daringfireball.net\\/index.xml\",\"title\":\"Daring Fireball\"}'),
	(4, 1, 'url', '{\"value\":\"http:\\/\\/feeds.feedburner.com\\/failblog\",\"title\":\"Epic Fail Funny Videos and Funny Pictures\"}'),
	(5, 1, 'url', '{\"value\":\"http:\\/\\/www.fastcompany.com\\/rss.xml\",\"title\":\"Fast Company\"}'),
	(6, 1, 'url', '{\"value\":\"http:\\/\\/gizmodo.com\\/index.xml\",\"title\":\"Gizmodo\"}');


-- ----------
-- Buckets
-- ----------
INSERT INTO buckets(id, account_id, bucket_name, bucket_name_url) VALUES
(1, 4, 'Bucket Number One', 'bucket-number-one'),
(2, 3, 'Bucket Two', 'bucket-two');


-- ------------------------------------
-- Data for table `bucket_collaborators`
-- -------------------------------------
INSERT INTO bucket_collaborators(bucket_id, account_id, read_only) VALUES
(1, 2, 0),
(1, 3, 1);


-- Data for table `river_followers`
INSERT INTO river_followers(river_id, account_id) VALUES 
(1, 6),
(1, 7);


-- Data for table `bucket_followers` 
INSERT INTO bucket_followers(bucket_id, account_id) VALUES
(1, 7),
(1, 6);

-- ---------------------------------
-- Data for table `buckets_droplets`
-- ---------------------------------
INSERT INTO `buckets_droplets`(`bucket_id`, `droplet_id`, `droplet_date_added`)
(SELECT 1 AS `bucket_id`, `id`, `droplet_date_add` FROM `droplets` WHERE `id` > 30 AND `id` <= 50);
