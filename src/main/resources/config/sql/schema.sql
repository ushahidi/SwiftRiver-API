-- -----------------------------------------------------
-- Version - 001
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table account_followers
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS account_followers (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint  NOT NULL,
  follower_id bigint  NOT NULL,
  follower_date_add TIMESTAMP NOT NULL,
  UNIQUE (account_id,follower_id)
);


-- -----------------------------------------------------
-- Table tags
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tags (
  id bigint NOT NULL,
  hash char(32) NOT NULL,
  tag varchar(50) NOT NULL,
  tag_canonical varchar(50) NOT NULL,
  tag_type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE  (hash)
);
CREATE INDEX tags_idx_tag_canonical ON tags (tag_canonical);  


-- -----------------------------------------------------
-- Table droplets_links
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS droplets_links (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_id bigint NOT NULL,
  link_id bigint NOT NULL,
  UNIQUE (droplet_id,link_id)
);
CREATE INDEX droplets_links_idx_link_id ON droplets_links (link_id);  
CREATE INDEX droplets_links_idx_droplet_id ON droplets_links (droplet_id);  


-- -----------------------------------------------------
-- Table rivers
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS rivers (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint NOT NULL,
  river_name varchar(255) NOT NULL,
  river_name_canonical varchar(255) NOT NULL,
  description varchar(255),
  river_active boolean default true,
  river_public boolean default false,
  default_layout varchar(10) DEFAULT 'list',
  max_drop_id bigint DEFAULT '0',
  drop_count int DEFAULT '0',
  drop_quota int DEFAULT '10000',
  river_full boolean default false,
  river_date_add timestamp,
  river_date_expiry timestamp,
  river_expired boolean default false,
  expiry_notification_sent boolean default false,
  extension_count int DEFAULT '0',
  public_token char(32) DEFAULT NULL,
  UNIQUE (account_id,river_name_canonical)
);
CREATE INDEX rivers_idx_account_id ON rivers (account_id);


-- -----------------------------------------------------
-- Table media
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS media (
  id bigint NOT NULL,
  hash char(32) NOT NULL,
  url varchar(2000) NOT NULL,
  type varchar(10) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (hash)
);


-- -----------------------------------------------------
-- Table media_thumbnails
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS media_thumbnails (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  media_id bigint NOT NULL,
  size int NOT NULL,
  url varchar(2000) NOT NULL,
  UNIQUE (media_id,size)
);
CREATE INDEX media_thumbnails_idx_media_id ON media_thumbnails (media_id);


-- -----------------------------------------------------
-- Table droplets_places
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS droplets_places (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_id bigint NOT NULL,
  place_id bigint NOT NULL,
  UNIQUE (droplet_id,place_id)
);
CREATE INDEX droplets_places_idx_droplet_id ON droplets_places (droplet_id);
CREATE INDEX droplets_places_idx_place_id ON droplets_places (place_id);


-- -----------------------------------------------------
-- Table account_droplet_media
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS  account_droplet_media (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id int NOT NULL,
  droplet_id bigint NOT NULL,
  media_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (account_id,droplet_id,media_id)
);


-- -----------------------------------------------------
-- Table rivers_droplets
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS rivers_droplets (
  id bigint NOT NULL,
  river_id bigint NOT NULL,
  river_channel_id bigint,
  droplet_id bigint NOT NULL,
  droplet_date_pub timestamp,
  PRIMARY KEY (id),
  UNIQUE (river_id,droplet_id)
);
CREATE INDEX rivers_idx_river_id ON rivers_droplets (river_id);
CREATE INDEX rivers_idx_droplet_id ON rivers_droplets (droplet_id);
CREATE INDEX rivers_idx_droplet_date_pub ON rivers_droplets (droplet_date_pub);
CREATE INDEX rivers_idx_channel_id ON rivers_droplets (river_channel_id);

-- ----------------------------------------
-- Table river_droplet_tags
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS river_droplet_tags (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rivers_droplets_id bigint NOT NULL,
  tag_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (rivers_droplets_id, tag_id)
);

-- ----------------------------------------
-- Table river_droplet_places
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS river_droplet_places (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rivers_droplets_id bigint NOT NULL,
  place_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (rivers_droplets_id, place_id)
);

-- ----------------------------------------
-- Table river_droplet_links
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS river_droplet_links (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rivers_droplets_id bigint NOT NULL,
  link_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (rivers_droplets_id, link_id)
);

-- ----------------------------------------
-- Table river_droplet_media
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS river_droplet_media (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rivers_droplets_id bigint NOT NULL,
  media_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (rivers_droplets_id, media_id)
);

-- -----------------------------------------------------
-- Table bucket_comments
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS bucket_comments (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  bucket_id bigint NOT NULL,
  account_id bigint NOT NULL,
  comment_text varchar(1000) NOT NULL,
  comment_date_add timestamp
);
CREATE INDEX bucket_comments_idx_comment_date_add ON bucket_comments (comment_date_add);
CREATE INDEX bucket_comments_idx_bucket_id_account_id ON bucket_comments (bucket_id,account_id);

-- -----------------------------------------------------
-- Table places
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS places (
  id bigint NOT NULL,
  hash char(32) NOT NULL,
  place_name varchar(50) NOT NULL,
  place_name_canonical varchar(50) NOT NULL,
  longitude float DEFAULT NULL,
  latitude float DEFAULT NULL,
  UNIQUE (hash)
);
CREATE INDEX places_idx_place_name_canonical ON places (place_name_canonical);


-- -----------------------------------------------------
-- Table buckets
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS buckets (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint NOT NULL,
  bucket_name varchar(255) NOT NULL,
  bucket_name_canonical VARCHAR(255) NOT NULL,
  bucket_description varchar(255),
  bucket_publish boolean default false,
  default_layout varchar(10) DEFAULT 'drops',
  bucket_date_add TIMESTAMP ,
  public_token varchar(32),
  drop_count INT DEFAULT '0',
  UNIQUE (account_id,bucket_name_canonical)
);
CREATE INDEX buckets_idx_account_id ON buckets (account_id);


-- -----------------------------------------------------
-- Table roles
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  name VARCHAR(32) NOT NULL ,
  description VARCHAR(255) DEFAULT NULL ,
  permissions VARCHAR(255) DEFAULT NULL ,
  user_id INT DEFAULT NULL,
  UNIQUE (name)
);


-- -----------------------------------------------------
-- Table identities
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS identities (
  id bigint NOT NULL,
  hash char(32) NOT NULL,
  channel varchar(20) NOT NULL,
  identity_orig_id varchar(255) DEFAULT NULL,
  identity_username varchar(255) DEFAULT NULL,
  identity_name varchar(255) DEFAULT NULL,
  identity_avatar varchar(255) DEFAULT NULL,
  identity_date_add TIMESTAMP ,
  identity_date_modified TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (hash)
);


-- -----------------------------------------------------
-- Table links
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS links (
  id bigint NOT NULL,
  hash char(32) NOT NULL,
  url varchar(2000) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (hash)
);


-- -----------------------------------------------------
-- Table plugins
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS plugins (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  plugin_path VARCHAR(100) NOT NULL ,
  plugin_name VARCHAR(255) NOT NULL,
  plugin_description VARCHAR(255) DEFAULT NULL,
  plugin_enabled boolean default false,
  plugin_weight boolean default true,
  plugin_installed boolean default false,
  UNIQUE (plugin_path)
);


-- -----------------------------------------------------
-- Table river_channels
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS river_channels (
  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  river_id int NOT NULL,
  channel varchar(100) NOT NULL,
  active boolean default true,
  parameters VARCHAR(2000),
  drop_count int DEFAULT '0',
  date_added TIMESTAMP ,
  date_modified TIMESTAMP
);


-- -----------------------------------------------------
-- Table users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email varchar(127) NOT NULL,
  name varchar(255) DEFAULT NULL,
  username varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  logins int DEFAULT '0',
  active boolean default true,
  invites smallint DEFAULT '10',
  last_login timestamp ,
  created_date timestamp ,
  expired boolean default false,
  locked boolean default false,
  UNIQUE (email),
  UNIQUE (username)
);


-- -----------------------------------------------------
-- Table user_tokens
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS user_tokens (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id bigint NOT NULL,
  token varchar(64) NOT NULL,
  created timestamp,
  expires timestamp,
  UNIQUE (token)
);
CREATE INDEX user_tokens_idx_user_id ON user_tokens (user_id);


-- -----------------------------------------------------
-- Table roles_users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS roles_users (
  user_id int NOT NULL,
  role_id int NOT NULL,
  account_id int NOT NULL,
  PRIMARY KEY (user_id,role_id)
);
CREATE INDEX roles_users_idx_account_id ON roles_users (account_id);
CREATE INDEX roles_users_idx_role_id ON roles_users (role_id);


-- -----------------------------------------------------
-- Table buckets_droplets
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS buckets_droplets (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  bucket_id int NOT NULL,
  droplet_id bigint NOT NULL,
  droplet_date_added timestamp,
  droplet_veracity int DEFAULT 1,
  UNIQUE (bucket_id,droplet_id)
);
CREATE INDEX buckets_droplets_idx_bucket_id ON buckets_droplets (bucket_id);
CREATE INDEX buckets_droplets_idx_droplet_id ON buckets_droplets (droplet_id);


-- ----------------------------------------
-- Table bucket_droplet_tags
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplet_tags (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  buckets_droplets_id bigint NOT NULL,
  tag_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (buckets_droplets_id, tag_id)
);

-- ----------------------------------------
-- Table bucket_droplet_places
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplet_places (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  buckets_droplets_id bigint NOT NULL,
  place_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (buckets_droplets_id, place_id)
);

-- ----------------------------------------
-- Table bucket_droplet_links
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplet_links (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  buckets_droplets_id bigint NOT NULL,
  link_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (buckets_droplets_id, link_id)
);

-- ----------------------------------------
-- Table bucket_droplet_media
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplet_media (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  buckets_droplets_id bigint NOT NULL,
  media_id bigint NOT NULL,
  deleted boolean default false,
  UNIQUE (buckets_droplets_id, media_id)
);

-- -----------------------------------------------------
-- Table droplets
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS droplets (
  id bigint NOT NULL,
  parent_id bigint,
  identity_id bigint NOT NULL,
  channel varchar(20) NOT NULL,
  droplet_hash char(32) NOT NULL,
  droplet_orig_id varchar(255) NOT NULL,
  droplet_type varchar(10) DEFAULT 'original',
  droplet_title varchar(255) DEFAULT NULL,
  droplet_content text,
  droplet_locale varchar(10),
  droplet_image bigint DEFAULT NULL,
  droplet_date_pub timestamp NOT NULL,
  droplet_date_add timestamp,
  processing_status boolean default false,
  original_url bigint DEFAULT NULL,
  comment_count int DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE (droplet_hash)
);
CREATE INDEX droplets_idx_droplet_date_pub ON droplets (droplet_date_pub);
CREATE INDEX droplets_idx_droplet_date_add ON droplets (droplet_date_add);
CREATE INDEX droplets_idx_processing_status ON droplets (processing_status);
CREATE INDEX droplets_idx_parent_id ON droplets (parent_id);
CREATE INDEX droplets_idx_droplet_image ON droplets (droplet_image);

-- -----------------------------------------------------
-- Table settings
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS settings (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  key VARCHAR(100) NOT NULL ,
  value VARCHAR(2000),
  UNIQUE (key)
);


-- -----------------------------------------------------
-- Table accounts
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS accounts (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id bigint NOT NULL,
  account_path varchar(100) DEFAULT NULL,
  account_private boolean default false,
  account_date_add timestamp,
  account_date_modified timestamp,
  account_active boolean default true,
  river_quota_remaining INT DEFAULT '1',
  version bigint  DEFAULT '1',
  UNIQUE (account_path)
);
CREATE INDEX accounts_idx_user_id ON accounts (user_id);

-- -----------------------------------------------------
-- Table droplets_media
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS droplets_media (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_id BIGINT NOT NULL,
  media_id BIGINT NOT NULL,
  UNIQUE (droplet_id, media_id)
);
CREATE INDEX droplets_media_idx_droplet_id ON droplets_media (droplet_id);
CREATE INDEX droplets_media_idx_media_id ON droplets_media (media_id);


-- -----------------------------------------------------
-- Table droplets_tags
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS droplets_tags (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_id bigint NOT NULL,
  tag_id bigint NOT NULL,
  UNIQUE (droplet_id,tag_id)
);
CREATE INDEX droplets_tags_idx_droplet_id ON droplets_tags (droplet_id);
CREATE INDEX droplets_tags_idx_tag_id ON droplets_tags (tag_id);



-- -----------------------------------------------------
-- Table user_actions
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS user_actions (
  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id bigint NOT NULL,
  action varchar(255) NOT NULL,
  action_on varchar(100) DEFAULT NULL,
  action_on_id bigint NOT NULL,
  action_to_id bigint DEFAULT NULL,
  action_date_add TIMESTAMP,
  confirmed boolean default false
);
CREATE INDEX user_actions_idx_user_id ON user_actions (user_id);
CREATE INDEX user_actions_idx_action_to_id ON user_actions (action_to_id);
CREATE INDEX user_actions_idx_action_on_id ON user_actions (action_on_id);
CREATE INDEX user_actions_idx_action_on ON user_actions (action_on);


-- ----------------------------------------
-- TABLE 'account_collaborators'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS account_collaborators (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint DEFAULT NULL,
  user_id bigint DEFAULT NULL,
  collaborator_active boolean default false,
  UNIQUE (account_id,user_id)
);


-- ----------------------------------------
-- TABLE 'river_collaborators'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS river_collaborators (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  river_id bigint DEFAULT NULL,
  account_id bigint DEFAULT NULL,
  collaborator_active boolean default false,
  read_only boolean default true,
  date_added TIMESTAMP,
  UNIQUE (river_id,account_id)
);


-- ----------------------------------------
-- TABLE 'bucket_collaborators'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_collaborators (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint NOT NULL,
  bucket_id bigint NOT NULL,
  collaborator_active boolean default false,
  read_only boolean default true,
  date_added TIMESTAMP,  
  UNIQUE (account_id,bucket_id)
);


-- ----------------------------------------
-- TABLE 'auth_tokens'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS auth_tokens (
  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  token varchar(32) NOT NULL,
  type varchar(20) DEFAULT NULL,
  data varchar(1000),
  created_date TIMESTAMP,
  expire_date TIMESTAMP,
  UNIQUE (token)
);


-- ----------------------------------------
-- TABLE 'bucket_followers'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_followers (
  bucket_id bigint NOT NULL,
  account_id bigint NOT NULL,
  UNIQUE (bucket_id,account_id)
);


-- ----------------------------------------
-- TABLE 'river_followers'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS river_followers (
  river_id bigint NOT NULL,
  account_id bigint NOT NULL,
  UNIQUE (river_id,account_id)
);


-- ----------------------------------------
-- TABLE 'droplet_scores'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS droplet_scores (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_id bigint NOT NULL,
  user_id bigint NOT NULL,
  score int NOT NULL,
  UNIQUE (droplet_id,user_id)
);


-- ----------------------------------------
-- TABLE 'trends'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS river_tag_trends (
  id bigint NOT NULL,
  hash char(32) NOT NULL,
  river_id bigint NOT NULL,
  date_pub timestamp NOT NULL,
  tag varchar(50) NOT NULL,
  tag_type varchar(20) NOT NULL,
  occurences bigint DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE (hash)
);
CREATE INDEX river_tag_trends_idx_river_id ON river_tag_trends (river_id);
CREATE INDEX river_tag_trends_idx_tag_type ON river_tag_trends (tag_type);


-- ----------------------------------------
-- TABLE 'bucket_comment_scores'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_comment_scores (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  bucket_comment_id bigint NOT NULL,
  user_id bigint NOT NULL,
  score boolean default false,
  score_date_add timestamp,
  score_date_modified timestamp,
  UNIQUE (bucket_comment_id,user_id)
);


-- -----------------------------------------------------
-- Table channel_quotas
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS channel_quotas (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  channel varchar(100) NOT NULL,
  channel_option varchar(100) NOT NULL,
  quota int DEFAULT 0,
  UNIQUE (channel,channel_option)
);


-- -----------------------------------------------------
-- Table account_channel_quotas
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS account_channel_quotas (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint NOT NULL,
  channel varchar(100) NOT NULL,
  channel_option varchar(100) NOT NULL,
  quota int DEFAULT '0',
  quota_used int DEFAULT '0',
  UNIQUE (account_id, channel,channel_option)
);
CREATE INDEX account_channel_quotas_idx_account_id ON account_channel_quotas (account_id);

-- ----------------------------------------
-- TABLE 'sequence'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS seq (
  name varchar(30) NOT NULL,
  id bigint NOT NULL,
  PRIMARY KEY (name)
);


-- ----------------------------------------
-- TABLE 'account_read_drops'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS account_read_drops (
  account_id bigint NOT NULL,
  droplet_id bigint NOT NULL,
  UNIQUE (droplet_id,account_id)
);


-- ------------------------------------
-- TABLE river_droplet_comments
-- ------------------------------------
CREATE TABLE IF NOT EXISTS river_droplet_comments (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rivers_droplets_id bigint NOT NULL,
  account_id bigint NOT NULL,
  comment_text varchar(1000) NOT NULL,
  comment_date_add timestamp
);
CREATE INDEX river_droplet_comments_idx_rivers_droplets_id ON river_droplet_comments (rivers_droplets_id);
CREATE INDEX river_droplet_comments_idx_account_id ON river_droplet_comments (account_id);


-- ----------------------------------------
-- TABLE bucket_droplet_comments
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplet_comments (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  buckets_droplets_id bigint NOT NULL,
  account_id bigint NOT NULL,
  comment_text varchar(1000) NOT NULL,
  comment_date_add timestamp
);
CREATE INDEX bucket_droplet_comments_idx_buckets_droplets_id ON bucket_droplet_comments (buckets_droplets_id);
CREATE INDEX bucket_droplet_comments_idx_account_id ON bucket_droplet_comments (account_id);



-- ----------------------------------------
-- TABLE 'clients'
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS clients (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint NOT NULL,
  client_id varchar(255) NOT NULL,
  client_secret varchar(255) NOT NULL,
  redirect_uri varchar(255) NOT NULL,
  name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  homepage varchar(255) DEFAULT NULL,
  active boolean default true
);
CREATE INDEX clients_idx_client_id ON clients (client_id);



-- -----------------------------------------------------
-- Table roles_clients
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS roles_clients (
  client_id int NOT NULL,
  role_id int NOT NULL,
  PRIMARY KEY (client_id,role_id)
);


-- -----------------------------------------------------
-- Table forms
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS forms (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint NOT NULL,
  name varchar(255) NOT NULL,
  date_added timestamp,
  date_modified timestamp,
  UNIQUE (name)
);
CREATE INDEX forms_idx_account_id ON forms (account_id);


-- -----------------------------------------------------
-- Table form_fields
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS form_fields (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  form_id bigint NOT NULL,
  title varchar(255) NOT NULL,
  description varchar(255),
  type varchar(50) NOT NULL,
  required boolean DEFAULT false,
  options varchar(1000),
  date_added timestamp,
  date_modified timestamp
);
CREATE INDEX forms_idx_form_id ON form_fields (form_id);

-- -----------------------------------------------------
-- Table river_droplet_form
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS river_droplet_form (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  drop_id bigint NOT NULL,
  form_id bigint NOT NULL,
  UNIQUE (drop_id, form_id)
);

-- -----------------------------------------------------
-- Table river_droplet_form_field
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS river_droplet_form_field (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_form_id bigint NOT NULL,
  field_id bigint NOT NULL,
  value varchar(1000),
  UNIQUE (droplet_form_id, field_id)
);

-- -----------------------------------------------------
-- Table bucket_droplet_form
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplet_form (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  drop_id bigint NOT NULL,
  form_id bigint NOT NULL,
  UNIQUE (drop_id, form_id)
);

-- -----------------------------------------------------
-- Table bucket_droplet_form_field
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplet_form_field (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_form_id bigint NOT NULL,
  field_id bigint NOT NULL,
  value varchar(1000),
  UNIQUE (droplet_form_id, field_id)
);

-- -----------------------------------------------------
-- Table river_rules
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS river_rules (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  river_id bigint NOT NULL,
  rule_name varchar(255) NOT NULL,
  rule_type boolean NOT NULL,
  rule_conditions text NOT NULL,
  rule_actions text NOT NULL,
  rule_date_add timestamp NOT NULL,
  rule_active boolean NOT NULL DEFAULT true,
  rule_all_conditions boolean NOT NULL DEFAULT true
);
CREATE INDEX river_rules_idx_river_id ON river_rules (river_id);

-- -----------------------------------------------------
-- Table river_droplets_read
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS river_droplets_read (
  account_id bigint NOT NULL,
  rivers_droplets_id bigint NOT NULL,
  UNIQUE (account_id,rivers_droplets_id)
);

-- -----------------------------------------------------
-- Table bucket_droplets_read
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS bucket_droplets_read (
  account_id bigint NOT NULL,
  buckets_droplets_id bigint NOT NULL,
  UNIQUE (account_id,buckets_droplets_id)
);

