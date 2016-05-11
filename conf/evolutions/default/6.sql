# create_auth_code

# --- !Ups
CREATE TABLE auth_code (
    authorization_code varchar(255) NOT NULL ,
    user_guid binary(16) NOT NULL,
    redirect_uri varchar(255),
    scope varchar(255),
    oauth_client_id varchar(255),
    expires_in int NOT NULL,
    created_date datetime NOT NULL,
    PRIMARY KEY (authorization_code)
);

# --- !Downs
DROP TABLE auth_code;
