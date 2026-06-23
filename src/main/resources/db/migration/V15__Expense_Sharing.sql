ALTER TABLE user
    ADD COLUMN connection_code VARCHAR(12) DEFAULT NULL;

UPDATE user
SET connection_code = CONCAT('USR', LPAD(id, 6, '0'))
WHERE connection_code IS NULL;

ALTER TABLE user
    MODIFY connection_code VARCHAR(12) NOT NULL,
    ADD CONSTRAINT uk_user_connection_code UNIQUE (connection_code);

CREATE TABLE user_connection(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    requester_user_id INT NOT NULL,
    target_user_id INT NOT NULL,
    status VARCHAR(100) NOT NULL,
    requested_at DATETIME(6) NOT NULL,
    responded_at DATETIME(6) DEFAULT NULL,
    FOREIGN KEY (requester_user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (target_user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

CREATE INDEX idx_user_connection_target_status
    ON user_connection(target_user_id, status, requested_at);

CREATE INDEX idx_user_connection_requester_status
    ON user_connection(requester_user_id, status, requested_at);

CREATE TABLE expense_share(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    expense_id INT NOT NULL,
    creator_user_id INT NOT NULL,
    participant_user_id INT NOT NULL,
    creator_amount DECIMAL(19,2) NOT NULL,
    participant_amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(100) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    settled_at DATETIME(6) DEFAULT NULL,
    FOREIGN KEY (expense_id) REFERENCES expense(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (creator_user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (participant_user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX uk_expense_share_expense
    ON expense_share(expense_id);

CREATE INDEX idx_expense_share_creator_status
    ON expense_share(creator_user_id, status, created_at);

CREATE INDEX idx_expense_share_participant_status
    ON expense_share(participant_user_id, status, created_at);
