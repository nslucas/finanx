CREATE INDEX idx_ft_user_type_occurred
    ON financial_transaction(user_id, type, occurred_at);

CREATE INDEX idx_ft_user_account_type_occurred
    ON financial_transaction(user_id, account_id, type, occurred_at);

CREATE INDEX idx_expense_user_card_purchase
    ON expense(user_id, card_id, purchase_date);

CREATE INDEX idx_expense_user_category_card
    ON expense(user_id, category_id, card_id);

CREATE INDEX idx_installment_due_expense
    ON expense_installment(due_date, expense_id);

CREATE INDEX idx_card_payment_user_statement
    ON card_payment(user_id, statement_year, statement_month);

CREATE INDEX idx_card_payment_user_card_statement
    ON card_payment(user_id, card_id, statement_year, statement_month);
