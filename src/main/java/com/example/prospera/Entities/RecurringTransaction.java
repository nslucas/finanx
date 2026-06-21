package com.example.prospera.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_transaction")
public class RecurringTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private RecurringTargetType targetType;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private RecurringFrequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer dayOfMonth;
    private Integer monthOfYear;
    private Integer accountId;
    private Integer cardId;
    private Integer categoryId;
    private Integer installmentCount;
    @Enumerated(EnumType.STRING)
    private RecurringClassification classification;
    private Boolean active;
    private Integer userId;

    public RecurringTransaction() {
    }

    public RecurringTransaction(Integer id, String name, String description, RecurringTargetType targetType,
                                TransactionType transactionType, BigDecimal amount, RecurringFrequency frequency,
                                LocalDate startDate, LocalDate endDate, Integer dayOfMonth, Integer monthOfYear,
                                Integer accountId, Integer cardId, Integer categoryId, Integer installmentCount,
                                RecurringClassification classification, Boolean active, Integer userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.targetType = targetType;
        this.transactionType = transactionType;
        this.amount = amount;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayOfMonth = dayOfMonth;
        this.monthOfYear = monthOfYear;
        this.accountId = accountId;
        this.cardId = cardId;
        this.categoryId = categoryId;
        this.installmentCount = installmentCount;
        this.classification = classification;
        this.active = active == null || active;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RecurringTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(RecurringTargetType targetType) {
        this.targetType = targetType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RecurringFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(RecurringFrequency frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Integer getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(Integer monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getInstallmentCount() {
        return installmentCount;
    }

    public void setInstallmentCount(Integer installmentCount) {
        this.installmentCount = installmentCount;
    }

    public RecurringClassification getClassification() {
        return classification;
    }

    public void setClassification(RecurringClassification classification) {
        this.classification = classification;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
