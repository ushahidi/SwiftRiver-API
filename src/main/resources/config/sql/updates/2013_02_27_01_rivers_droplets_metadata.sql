/*
 * Metadata tables for the drops in  river
 */

-- ----------------------------------------
-- Table `river_droplet_tags`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `river_droplet_tags` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `rivers_droplets_id` bigint(20) NOT NULL,
  `tag_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_river_droplet_tag` (`rivers_droplets_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table `river_droplet_places`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `river_droplet_places` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `rivers_droplets_id` bigint(20) NOT NULL,
  `place_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_river_droplet_place` (`rivers_droplets_id`, `place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table `river_droplet_links`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `river_droplet_links` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `rivers_droplets_id` bigint(20) NOT NULL,
  `link_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_river_droplet_link` (`rivers_droplets_id`, `link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------
-- Table `river_droplet_media`
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `river_droplet_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `rivers_droplets_id` bigint(20) NOT NULL,
  `media_id` bigint(20) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_river_droplet_media` (`rivers_droplets_id`, `media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;