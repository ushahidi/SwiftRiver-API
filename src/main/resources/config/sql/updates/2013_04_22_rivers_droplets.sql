ALTER TABLE `rivers_droplets` DROP `channel`;
ALTER TABLE `rivers_droplets` CHANGE `channel_id` `river_channel_id` BIGINT(20)  UNSIGNED  NULL  DEFAULT '0';
