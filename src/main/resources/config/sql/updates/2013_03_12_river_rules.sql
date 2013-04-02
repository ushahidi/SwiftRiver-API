CREATE TABLE IF NOT EXISTS `river_rules` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `river_id` bigint(20) unsigned NOT NULL,
  `rule_name` varchar(255) NOT NULL DEFAULT '',
  `rule_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Possible values are 1=preprocessing, 2=postprocessing',
  `rule_conditions` text NOT NULL,
  `rule_actions` text NOT NULL,
  `rule_date_add` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `rule_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_river_id` (`river_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;