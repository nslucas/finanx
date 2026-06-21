package com.example.prospera.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime occurredAt;
    private String description;
    private Integer accountId;
    private Integer relatedTransactionId;
    private Integer userId;
    private Integer categoryId;

    public Transaction() {
    }

    public Transaction(Integer id, TransactionType type, BigDecimal amount, LocalDateTime occurredAt,
                       String description, Integer accountId, Integer relatedTransactionId, Integer userId) {
        this(id, type, amount, occurredAt, description, accountId, relatedTransactionId, userId, null);
    }

    public Transaction(Integer id, TransactionType type, BigDecimal amount, LocalDateTime occurredAt,
                       String description, Integer accountId, Integer relatedTransactionId, Integer userId,
                       Integer categoryId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.occurredAt = occurredAt;
        this.description = description;
        this.accountId = accountId;
        this.relatedTransactionId = relatedTransactionId;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getRelatedTransactionId() {
        return relatedTransactionId;
    }

    public void setRelatedTransactionId(Integer relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
