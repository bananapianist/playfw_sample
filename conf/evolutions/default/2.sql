# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

CREATE TABLE token (
  uuid varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  creation_time datetime NOT NULL,
  expiration_time datetime NOT NULL,
  is_sign_up boolean NOT NULL
);

CREATE TABLE user (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id varchar(255) NOT NULL,
  provider_id varchar(255) NOT NULL,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  full_name varchar(255) NOT NULL,
  email varchar(255),
  avatar_url varchar(255),
  auth_method varchar(255) NOT NULL,
  token varchar(255),
  secret varchar(255),
  access_token varchar(255),
  token_type varchar(255),
  expires_in Int,
  refresh_token varchar(255),
  PRIMARY KEY (id)
);

# --- !Downs

drop table token;
drop table user;

