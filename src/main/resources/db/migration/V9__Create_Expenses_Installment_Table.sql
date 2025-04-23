CREATE TABLE expense_installment(
    expense_id INT NOT NULL,
    installment_number int NOT NULL,
    installment_amount double NOT NULL,
    due_date date NOT NULL,
    PRIMARY KEY (expense_id, installment_number),
    FOREIGN KEY (expense_id) REFERENCES expense(id)
);