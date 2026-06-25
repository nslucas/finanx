CREATE TABLE user_preference(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    default_movement_kind VARCHAR(100) NOT NULL,
    default_account_id INT DEFAULT NULL,
    default_target_account_id INT DEFAULT NULL,
    default_card_id INT DEFAULT NULL,
    default_expense_category_id INT DEFAULT NULL,
    default_income_category_id INT DEFAULT NULL,
    default_installment_count INT NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (default_account_id) REFERENCES account(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
    FOREIGN KEY (default_target_account_id) REFERENCES account(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
    FOREIGN KEY (default_card_id) REFERENCES card(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
    FOREIGN KEY (default_expense_category_id) REFERENCES category(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
    FOREIGN KEY (default_income_category_id) REFERENCES category(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX uk_user_preference_user
    ON user_preference(user_id);
