-- ------------------------------------
-- TABLE `river_droplet_comments`
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `river_droplet_comments` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `rivers_droplets_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `comment_text` text NOT NULL,
  `comment_date_add` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `idx_rivers_droplets_id` (`rivers_droplets_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- TABLE `bucket_droplet_comments`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `bucket_droplet_comments` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `buckets_droplets_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `comment_text` text NOT NULL,
  `comment_date_add` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `idx_buckets_droplets_id` (`buckets_droplets_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Migrate the comments
INSERT INTO `river_droplet_comments` (`rivers_droplets_id`, `account_id`, `comment_text`, `comment_date_add`)
(SELECT `rivers_droplets`.`id`, `accounts`.`id`, `comment_text`, `date_added`
 FROM `droplet_comments`
 INNER JOIN `rivers_droplets` ON (`rivers_droplets`.`droplet_id` = `droplet_comments`.`droplet_id`)
 INNER JOIN `accounts` ON (`accounts`.`user_id` = `droplet_comments`.`user_id`));

INSERT INTO `bucket_droplet_comments` (`buckets_droplets_id`, `account_id`, `comment_text`, `comment_date_add`)
(SELECT `buckets_droplets`.`id`, `accounts`.`id`, `comment_text`, `date_added`
 FROM `droplet_comments`
 INNER JOIN `buckets_droplets` ON (`buckets_droplets`.`droplet_id` = `droplet_comments`.`droplet_id`)
 INNER JOIN `accounts` ON (`accounts`.`user_id` = `droplet_comments`.`user_id`));

-- Drop `droplet_comments`
DROP TABLE IF EXISTS `droplet_comments`;
