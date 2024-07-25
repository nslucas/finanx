CREATE TABLE wallet(
                      id varchar(255) NOT NULL PRIMARY KEY,
                      owner varchar(255) DEFAULT NULL,
                      balance double DEFAULT NULL,
                      cards_id varchar(255) DEFAULT NULL,
                      user_id bigint DEFAULT NULL,
                      FOREIGN KEY (cards_id) REFERENCES card(id)
                      ON DELETE CASCADE
                      ON UPDATE CASCADE,
                      FOREIGN KEY (user_id) REFERENCES user(id)
                      ON DELETE RESTRICT
                      ON UPDATE RESTRICT
);