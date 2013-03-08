DROP TABLE IF EXISTS `droplet_comments`;

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