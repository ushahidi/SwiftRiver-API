DROP TABLE IF EXISTS `account_read_drops`;

-- -----------------------------------------------------
-- Table `river_droplets_read`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `river_droplets_read` (
  `account_id` bigint(20) NOT NULL,
  `rivers_droplets_id` bigint(20) NOT NULL,
  UNIQUE KEY `idx_account_river_droplet_id` (`account_id`,`rivers_droplets_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Maintains the list of read river drops';