CREATE TABLE card(
                      id varchar(255) NOT NULL PRIMARY KEY,
                      number varchar(255) DEFAULT NULL,
                      owner varchar(255) DEFAULT NULL,
                      credit_limit float(10) DEFAULT NULL,
                      balance float(10) DEFAULT NULL,
                      user_id bigint DEFAULT NULL
);
