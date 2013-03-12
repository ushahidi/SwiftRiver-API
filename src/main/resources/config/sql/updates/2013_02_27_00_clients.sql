-- ----------------------------------------
-- TABLE 'clients'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `clients` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `client_id` varchar(255) NOT NULL DEFAULT '',
  `client_secret` varchar(255) NOT NULL DEFAULT '',
  `redirect_uri` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `homepage` varchar(255) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `client_id` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `roles_clients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles_clients` (
  `client_id` int(11) unsigned NOT NULL,
  `role_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`client_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `roles` (`id`, `name`, `description`, `permissions`, `user_id`) VALUES (NULL, 'client', 'Client application', NULL, NULL);
INSERT INTO `roles` (`id`, `name`, `description`, `permissions`, `user_id`) VALUES (NULL, 'trusted_client', 'Trusted client application', NULL, NULL);
