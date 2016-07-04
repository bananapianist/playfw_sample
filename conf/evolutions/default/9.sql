# create_oauth_user

# --- !Ups
CREATE TABLE oauth_user (
    guid         binary(16) NOT NULL PRIMARY KEY,
    name       varchar(255) NOT NULL,
   	created_date datetime NOT NULL
);

# --- !Downs
DROP TABLE oauth_user;

