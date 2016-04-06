# create_account

# --- !Ups
CREATE TABLE account (
    id         INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email      varchar(255) NOT NULL UNIQUE,
    password   varchar(255) NOT NULL,
    name       varchar(255) NOT NULL,
    role       varchar(255) NOT NULL
);

# --- !Downs
DROP TABLE account;
