CREATE TABLE category(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    active BIT(1) NOT NULL DEFAULT b'1',
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

CREATE INDEX idx_category_user_active_name
    ON category(user_id, active, name);

CREATE TABLE budget(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    budget_month INT NOT NULL,
    budget_year INT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    active BIT(1) NOT NULL DEFAULT b'1',
    user_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

CREATE INDEX idx_budget_user_month
    ON budget(user_id, active, budget_year, budget_month);

CREATE INDEX idx_budget_user_category_month
    ON budget(user_id, category_id, active, budget_year, budget_month);

ALTER TABLE expense
    ADD COLUMN category_id INT DEFAULT NULL;

ALTER TABLE expense
    ADD CONSTRAINT fk_expense_category
    FOREIGN KEY (category_id) REFERENCES category(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT;

CREATE INDEX idx_expense_category
    ON expense(user_id, category_id);

ALTER TABLE financial_transaction
    ADD COLUMN category_id INT DEFAULT NULL;

ALTER TABLE financial_transaction
    ADD CONSTRAINT fk_financial_transaction_category
    FOREIGN KEY (category_id) REFERENCES category(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT;

CREATE INDEX idx_financial_transaction_category_month
    ON financial_transaction(user_id, category_id, type, occurred_at);
