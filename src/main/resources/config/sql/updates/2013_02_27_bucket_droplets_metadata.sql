/*
 * Metadata tables for bucket drops
 */

-- ----------------------------------------
-- Table `bucket_droplet_tags`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `bucket_droplet_tags` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `buckets_droplets_id` bigint(20) NOT NULL,
  `tag_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_bucket_droplet_tag` (`buckets_droplets_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table `bucket_droplet_places`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `bucket_droplet_places` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `buckets_droplets_id` bigint(20) NOT NULL,
  `place_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_bucket_droplet_place` (`buckets_droplets_id`, `place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table `bucket_droplet_links`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `bucket_droplet_links` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `buckets_droplets_id` bigint(20) NOT NULL,
  `link_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_bucket_droplet_link` (`buckets_droplets_id`, `link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table `bucket_droplet_media`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `bucket_droplet_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `buckets_droplets_id` bigint(20) NOT NULL,
  `media_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_bucket_droplet_media` (`buckets_droplets_id`, `media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
