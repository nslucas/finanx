CREATE TABLE account(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    active BIT(1) NOT NULL DEFAULT b'1',
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

CREATE TABLE financial_transaction(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(100) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    occurred_at DATETIME(6) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    account_id INT NOT NULL,
    related_transaction_id INT DEFAULT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

ALTER TABLE financial_transaction
    ADD CONSTRAINT fk_financial_transaction_related
    FOREIGN KEY (related_transaction_id) REFERENCES financial_transaction(id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT;

CREATE TABLE card_payment(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    card_id INT NOT NULL,
    account_id INT NOT NULL,
    user_id INT NOT NULL,
    statement_month INT NOT NULL,
    statement_year INT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    payment_date DATE NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    transaction_id INT NOT NULL,
    FOREIGN KEY (card_id) REFERENCES card(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (account_id) REFERENCES account(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    FOREIGN KEY (transaction_id) REFERENCES financial_transaction(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

INSERT INTO account(name, type, balance, currency, active, user_id)
SELECT COALESCE(owner, 'Legacy account'), 'CHECKING', COALESCE(balance, 0.00), 'BRL', b'1', user_id
FROM wallet
WHERE user_id IS NOT NULL;

INSERT INTO financial_transaction(type, amount, occurred_at, description, account_id, user_id)
SELECT 'ADJUSTMENT', a.balance, NOW(6), 'Initial balance migrated from wallet', a.id, a.user_id
FROM account a
WHERE a.balance <> 0.00;
