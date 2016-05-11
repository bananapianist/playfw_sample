# create_oauth_client

# --- !Ups
CREATE TABLE oauth_client (
    oauth_client_id varchar(255) NOT NULL ,
    application_id bigint(20) NOT NULL ,
    client_secret varchar(255),
    redirect_uri varchar(255),
    grant_type varchar(255),
    expires_in int NOT NULL,
    created_date datetime NOT NULL,
    PRIMARY KEY (oauth_client_id)
);

# --- !Downs
DROP TABLE oauth_client;
