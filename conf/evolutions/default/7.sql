# create_access_token

# --- !Ups
CREATE TABLE access_token (
    access_token varchar(255) NOT NULL ,
    refresh_token varchar(255),
    user_guid binary(16) NOT NULL,
    redirect_uri varchar(255),
    scope varchar(255),
    oauth_client_id varchar(255),
    expires_in int NOT NULL,
    created_date datetime NOT NULL,
    PRIMARY KEY (access_token)
);

# --- !Downs
DROP TABLE access_token;

