CREATE TABLE IF NOT EXISTS forms (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id bigint NOT NULL,
  name varchar(255) NOT NULL,
  date_added timestamp,
  date_modified timestamp,
  UNIQUE (name)
);
CREATE INDEX forms_idx_account_id ON forms (account_id);

CREATE TABLE IF NOT EXISTS form_fields (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  form_id bigint NOT NULL,
  title varchar(255) NOT NULL,
  description varchar(255),
  type varchar(50) NOT NULL,
  required boolean DEFAULT false,
  options varchar(1000) NOT NULL,
  date_added timestamp,
  date_modified timestamp,
);
CREATE INDEX forms_idx_form_id ON form_fields (form_id);