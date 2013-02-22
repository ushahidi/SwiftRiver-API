CREATE TABLE `account_read_drops` (
  `account_id` bigint(20) NOT NULL,
  `droplet_id` bigint(20) NOT NULL,
  UNIQUE KEY `bucket_id` (`droplet_id`,`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;