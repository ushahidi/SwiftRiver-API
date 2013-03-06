/*
 * Migrate the links from `account_droplet_links`
 */
INSERT INTO `bucket_droplet_links` (`buckets_droplets_id`, `link_id`, `deleted`)
(SELECT `buckets_droplets`.`id`, `link_id`, `deleted` FROM `account_droplet_links`
 INNER JOIN buckets_droplets ON (`account_droplet_links`.`droplet_id` = buckets_droplets.droplet_id));

INSERT INTO `river_droplet_links` (`rivers_droplets_id`, `link_id`, `deleted`)
(SELECT `rivers_droplets`.`id`, `link_id`, `deleted` FROM `account_droplet_links`
	INNER JOIN `rivers_droplets` ON (`account_droplet_links`.`droplet_id` = `rivers_droplets`.`droplet_id`));

/*
 * Migrate the `places` from `account_droplet_places` 
 */
INSERT INTO `bucket_droplet_places` (`buckets_droplets_id`, `place_id`, `deleted`)
(SELECT `buckets_droplets`.`id`, `place_id`, `deleted` FROM `account_droplet_places`
 INNER JOIN `buckets_droplets` ON (`account_droplet_places`.`droplet_id` = `buckets_droplets`.`droplet_id`));

INSERT INTO `river_droplet_places` (`rivers_droplets_id`, `place_id`, `deleted`)
(SELECT `rivers_droplets`.`id`, `place_id`, `deleted` FROM `account_droplet_places`
 INNER JOIN `rivers_droplets` ON (`account_droplet_places`.`droplet_id` = `rivers_droplets`.`droplet_id`));

/*
 * Migrate the tags from `account_droplet_places` 
 */
INSERT INTO `bucket_droplet_tags` (`buckets_droplets_id`, `tag_id`, `deleted`)
(SELECT `buckets_droplets`.`id`, `tag_id`, `deleted` FROM `account_droplet_tags`
INNER JOIN `buckets_droplets` ON (`account_droplet_tags`.`droplet_id` = `buckets_droplets`.`droplet_id`));

INSERT INTO `river_droplet_tags` (`rivers_droplets_id`, `tag_id`, `deleted`)
(SELECT `rivers_droplets`.`id`, `tag_id`, `deleted` FROM `account_droplet_tags`
INNER JOIN `rivers_droplets` ON (`account_droplet_tags`.`droplet_id` = `rivers_droplets`.`droplet_id`));

/*
 * Migrate the media from `account_droplet_media`
 */
INSERT INTO `bucket_droplet_media` (`buckets_droplets_id`, `media_id`, `deleted`)
(SELECT `buckets_droplets`.`id`, `media_id`, `deleted` FROM `account_droplet_media`
INNER JOIN `buckets_droplets` ON (`account_droplet_media`.`droplet_id` = `buckets_droplets`.`droplet_id`));

INSERT INTO `river_droplet_media` (`rivers_droplets_id`, `media_id`, `deleted`)
(SELECT `rivers_droplets`.`id`, `media_id`, `deleted` FROM `account_droplet_media`
INNER JOIN `rivers_droplets` ON (`account_droplet_media`.`droplet_id` = `rivers_droplets`.`droplet_id`));

/*
 * Drop account_droplet metadata tables
 */
DROP TABLE IF EXISTS `account_droplet_links`;
DROP TABLE IF EXISTS `account_droplet_places`;
DROP TABLE IF EXISTS `account_droplet_tags`;
DROP TABLE IF EXISTS `account_droplet_media`;