# create_bill

# --- !Ups
CREATE TABLE bill (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    contract_id bigint(20) NOT NULL ,
    bill_name varchar(255),
    bill_email varchar(255),
    bill_tel varchar(255),
    bill_address varchar(255),
    created_date datetime,
    updated_date timestamp,
    PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE bill;
