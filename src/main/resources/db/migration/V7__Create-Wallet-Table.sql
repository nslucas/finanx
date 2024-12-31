CREATE TABLE wallet(
                      id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                      owner varchar(255) DEFAULT NULL,
                      balance double DEFAULT NULL,
                      cards_id INT DEFAULT NULL,
                      user_id bigint DEFAULT NULL,
                      FOREIGN KEY (cards_id) REFERENCES card(id)
                      ON DELETE RESTRICT
                      ON UPDATE RESTRICT,
                      FOREIGN KEY (user_id) REFERENCES user(id)
                      ON DELETE RESTRICT
                      ON UPDATE RESTRICT
);