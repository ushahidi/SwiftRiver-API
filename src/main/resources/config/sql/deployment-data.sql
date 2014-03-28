-- -----------------------------------------------------
-- Basic data for clean deployment
-- -----------------------------------------------------
INSERT INTO `clients`
VALUES
('1', '1', 'trusted-client', '8b22f281afd911c3dfc59270af43db1995d5968a3447c780ba3e152e603fd9a0', 'http://localhost:8080/oauth/redirect', 'my app', 'my app\'s description', 'my app\'s homepage', '1');

INSERT INTO `roles_clients`
VALUES (1,3), (1,4);

INSERT INTO `roles` (`id`,`name`,`description`)
VALUES 
	(1, 'user', 'Login privileges, granted after account confirmation'),
	(2, 'admin', 'Super Administrator'),
	(3, 'client', 'Client application'),
	(4, 'trusted_client', 'Truested client application');

INSERT INTO `accounts`
VALUES ('1', '1', 'default', '0', '2013-01-01 00:00:00', '2013-01-02 00:00:00', '1', '10', '1');

INSERT INTO `users`
VALUES ('1', 'myswiftriver@myswiftriver.com', 'Administrator', 'admin', '$2a$05$f0I9XjamKm4LEaF8av1Zy.tzBrzFM0smLMKvMAqUWicGAcEnkCdQe', '0', '1', '10', '2014-03-08 13:21:15', '2013-01-01 00:00:00', '0', '0');

INSERT INTO `roles_users`
VALUES
	(1,1,1),
	(1,2,1);

-- Core API needs these up or else it throws a Null Pointer exception
INSERT INTO seq (name, id) VALUES
  ('droplets', 10),
  ('tags', 11),
  ('places', 13),
  ('links', 10),
  ('identities', 2),
  ('media', 10),
  ('river_tag_trends', 11),
  ('rivers_droplets', 5);
