# create_app_account

# --- !Ups
CREATE TABLE app_account (
    id         bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       varchar(255) NOT NULL,
   	created_date datetime NOT NULL
);

# --- !Downs
DROP TABLE app_account;

