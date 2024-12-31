CREATE TABLE user(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) DEFAULT NULL,
    last_name varchar(255) DEFAULT NULL,
    email varchar(255) DEFAULT NULL,
    month_limit double DEFAULT NULL,
    password varchar(255) DEFAULT NULL
);