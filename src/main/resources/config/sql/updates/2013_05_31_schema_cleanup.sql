-- Schema cleanup - Prune unused tables from the schema
DROP TABLE IF EXISTS `settings`;
DROP TABLE IF EXISTS `account_channel_quotas`;
DROP TABLE IF EXISTS `plugins`;
DROP TABLE IF EXISTS `account_droplet_media`;
DROP TABLE IF EXISTS `account_collaborators`;
DROP TABLE IF EXISTS `account_read_drops`;
DROP TABLE IF EXISTS `channel_quotas`;
DROP TABLE IF EXISTS `user_subscriptions`;
DROP TABLE IF EXISTS `snapshots`;
DROP TABLE IF EXISTS `user_identities`;
DROP TABLE IF EXISTS `account_plugins`;
DROP TABLE IF EXISTS `sources_identities`;
DROP TABLE IF EXISTS `twitter_crawls`;