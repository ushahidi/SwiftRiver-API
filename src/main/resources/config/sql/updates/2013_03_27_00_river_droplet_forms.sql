CREATE TABLE IF NOT EXISTS river_droplet_form (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  river_droplets_id bigint NOT NULL,
  form_id bigint NOT NULL,
  UNIQUE (rivers_droplets_id, form_id)
);

CREATE TABLE IF NOT EXISTS river_droplet_form_field (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  droplet_form_id bigint NOT NULL,
  field_id bigint NOT NULL,
  value varchar(1000),
  UNIQUE (droplet_form_id, field_id)
);