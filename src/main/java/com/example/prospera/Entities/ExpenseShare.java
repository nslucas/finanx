package com.example.prospera.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_share")
public class ExpenseShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer expenseId;
    private Integer creatorUserId;
    private Integer participantUserId;
    private BigDecimal creatorAmount;
    private BigDecimal participantAmount;
    @Enumerated(EnumType.STRING)
    private ExpenseShareStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime settledAt;

    public ExpenseShare() {
    }

    public ExpenseShare(Integer id, Integer expenseId, Integer creatorUserId, Integer participantUserId,
                        BigDecimal creatorAmount, BigDecimal participantAmount, ExpenseShareStatus status,
                        LocalDateTime createdAt, LocalDateTime settledAt) {
        this.id = id;
        this.expenseId = expenseId;
        this.creatorUserId = creatorUserId;
        this.participantUserId = participantUserId;
        this.creatorAmount = creatorAmount;
        this.participantAmount = participantAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.settledAt = settledAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public Integer getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(Integer creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public Integer getParticipantUserId() {
        return participantUserId;
    }

    public void setParticipantUserId(Integer participantUserId) {
        this.participantUserId = participantUserId;
    }

    public BigDecimal getCreatorAmount() {
        return creatorAmount;
    }

    public void setCreatorAmount(BigDecimal creatorAmount) {
        this.creatorAmount = creatorAmount;
    }

    public BigDecimal getParticipantAmount() {
        return participantAmount;
    }

    public void setParticipantAmount(BigDecimal participantAmount) {
        this.participantAmount = participantAmount;
    }

    public ExpenseShareStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseShareStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(LocalDateTime settledAt) {
        this.settledAt = settledAt;
    }
}
