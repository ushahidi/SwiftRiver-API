-- -----------------------------------------------------
-- Table `bucket_droplets_read`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bucket_droplets_read` (
  `account_id` bigint(20) NOT NULL,
  `buckets_droplets_id` bigint(20) NOT NULL,
  UNIQUE KEY `idx_account_bucket_droplet_id` (`account_id`,`buckets_droplets_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maintains the list of read bucket drops';