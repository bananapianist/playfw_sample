# create_customer

# --- !Ups
CREATE TABLE customer (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255),
    email varchar(255),
    tel varchar(255),
    address varchar(255),
    comment mediumtext,
    action_date datetime,
    notification_period integer,
    notification_date datetime,
    is_disabled boolean,
    created_date datetime,
    updated_date timestamp,
    PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE customer;
