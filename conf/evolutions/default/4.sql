# create_contract

# --- !Ups
CREATE TABLE contract (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    customer_id bigint(20) NOT NULL ,
    status varchar(50),
    comment mediumtext,
    contract_date datetime,
    cancel_date datetime,
    is_disabled boolean,
    created_date datetime,
    updated_date timestamp,
    PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE contract;
