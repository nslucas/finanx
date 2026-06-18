package com.example.finanx.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "card_payment")
public class CardPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer cardId;
    private Integer accountId;
    private Integer userId;
    @Column(name = "statement_month")
    private Integer month;
    @Column(name = "statement_year")
    private Integer year;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String description;
    private Integer transactionId;

    public CardPayment() {
    }

    public CardPayment(Integer id, Integer cardId, Integer accountId, Integer userId, Integer month, Integer year,
                       BigDecimal amount, LocalDate paymentDate, String description, Integer transactionId) {
        this.id = id;
        this.cardId = cardId;
        this.accountId = accountId;
        this.userId = userId;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.description = description;
        this.transactionId = transactionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
}
