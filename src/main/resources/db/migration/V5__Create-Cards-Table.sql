CREATE TABLE card(
                      id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                      number varchar(255) DEFAULT NULL,
                      owner varchar(255) DEFAULT NULL,
                      credit_limit float(10) DEFAULT NULL,
                      balance float(10) DEFAULT NULL,
                      user_id INT DEFAULT NULL
);
