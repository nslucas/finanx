CREATE TABLE expense(
    id varchar(255) NOT NULL PRIMARY KEY,
    name varchar(255) DEFAULT NULL,
    amount double DEFAULT NULL,
    installment_count int DEFAULT NULL,
    purchase_date datetime(6) DEFAULT NULL,
    description varchar(255) DEFAULT NULL,
    user_id bigint DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);