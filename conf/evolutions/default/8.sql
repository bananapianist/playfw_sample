# create_oauth_client

# --- !Ups
CREATE TABLE oauth_client (
    oauth_client_id binary(16) NOT NULL ,
    oauth_user_id binary(16) NOT NULL ,
    client_secret varchar(255),
    redirect_uri varchar(255),
    grant_type varchar(255),
    expires_in int NOT NULL,
    created_date datetime NOT NULL,
    PRIMARY KEY (oauth_client_id)
);

# --- !Downs
DROP TABLE oauth_client;
