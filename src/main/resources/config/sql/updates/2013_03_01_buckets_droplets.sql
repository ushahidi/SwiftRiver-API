-- Add droplet_veracity column to track the popularity of a drop
ALTER TABLE `buckets_droplets` ADD `droplet_veracity` INT(11)  UNSIGNED  NULL  DEFAULT '1'  AFTER `droplet_date_added`;
