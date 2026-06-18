CREATE TABLE recurring_transaction(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    target_type VARCHAR(100) NOT NULL,
    transaction_type VARCHAR(100) DEFAULT NULL,
    amount DECIMAL(19,2) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE DEFAULT NULL,
    day_of_month INT NOT NULL,
    month_of_year INT DEFAULT NULL,
    account_id INT DEFAULT NULL,
    card_id INT DEFAULT NULL,
    category_id INT DEFAULT NULL,
    installment_count INT DEFAULT NULL,
    classification VARCHAR(100) NOT NULL,
    active BIT(1) NOT NULL DEFAULT b'1',
    user_id INT NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (card_id) REFERENCES card(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (category_id) REFERENCES category(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

CREATE INDEX idx_recurring_transaction_user_range
    ON recurring_transaction(user_id, active, start_date, end_date);

CREATE TABLE recurring_occurrence(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    recurrence_id INT NOT NULL,
    occurrence_date DATE NOT NULL,
    status VARCHAR(100) NOT NULL,
    transaction_id INT DEFAULT NULL,
    expense_id INT DEFAULT NULL,
    user_id INT NOT NULL,
    CONSTRAINT uk_recurring_occurrence_date UNIQUE (recurrence_id, occurrence_date),
    FOREIGN KEY (recurrence_id) REFERENCES recurring_transaction(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (transaction_id) REFERENCES financial_transaction(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
    FOREIGN KEY (expense_id) REFERENCES expense(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

CREATE INDEX idx_recurring_occurrence_user_date
    ON recurring_occurrence(user_id, occurrence_date, status);
