ALTER TABLE user
    MODIFY month_limit DECIMAL(19,2) DEFAULT NULL;

UPDATE user
SET role = UPPER(role);

ALTER TABLE user
    MODIFY role VARCHAR(100) NOT NULL DEFAULT 'USER';

ALTER TABLE card
    ADD COLUMN bank_name VARCHAR(255) DEFAULT NULL,
    ADD COLUMN name VARCHAR(255) DEFAULT NULL,
    ADD COLUMN network VARCHAR(100) DEFAULT NULL,
    ADD COLUMN last_four_digits VARCHAR(4) DEFAULT NULL,
    ADD COLUMN closing_day INT DEFAULT NULL,
    ADD COLUMN due_day INT DEFAULT NULL,
    ADD COLUMN active BIT(1) NOT NULL DEFAULT b'1';

UPDATE card
SET bank_name = 'Unknown bank',
    name = COALESCE(owner, 'Legacy card'),
    network = 'Unknown',
    last_four_digits = RIGHT(CAST(number AS CHAR), 4),
    closing_day = 25,
    due_day = 10,
    active = b'1'
WHERE bank_name IS NULL;

ALTER TABLE card
    MODIFY credit_limit DECIMAL(19,2) DEFAULT NULL;

ALTER TABLE expense
    ADD COLUMN card_id INT DEFAULT NULL,
    MODIFY amount DECIMAL(19,2) DEFAULT NULL;

UPDATE expense
SET card_id = 1
WHERE user_id = 1
  AND EXISTS (SELECT 1 FROM card WHERE id = 1);

ALTER TABLE expense_installment
    MODIFY installment_amount DECIMAL(19,2) NOT NULL;

ALTER TABLE wallet
    MODIFY balance DECIMAL(19,2) DEFAULT NULL;

ALTER TABLE card
    ADD CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;

ALTER TABLE expense
    ADD CONSTRAINT fk_expense_card FOREIGN KEY (card_id) REFERENCES card(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;
