package com.example.prospera.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preference")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    @Enumerated(EnumType.STRING)
    private MovementKind defaultMovementKind;
    private Integer defaultAccountId;
    private Integer defaultTargetAccountId;
    private Integer defaultCardId;
    private Integer defaultExpenseCategoryId;
    private Integer defaultIncomeCategoryId;
    private Integer defaultInstallmentCount;
    private Boolean notifyConnectionRequests;
    private Boolean notifySharedExpenses;
    private Boolean notifyFinancialDigest;

    public UserPreference() {
    }

    public UserPreference(Integer id, Integer userId, MovementKind defaultMovementKind, Integer defaultAccountId,
                          Integer defaultTargetAccountId, Integer defaultCardId, Integer defaultExpenseCategoryId,
                          Integer defaultIncomeCategoryId, Integer defaultInstallmentCount) {
        this.id = id;
        this.userId = userId;
        this.defaultMovementKind = defaultMovementKind;
        this.defaultAccountId = defaultAccountId;
        this.defaultTargetAccountId = defaultTargetAccountId;
        this.defaultCardId = defaultCardId;
        this.defaultExpenseCategoryId = defaultExpenseCategoryId;
        this.defaultIncomeCategoryId = defaultIncomeCategoryId;
        this.defaultInstallmentCount = defaultInstallmentCount;
        this.notifyConnectionRequests = true;
        this.notifySharedExpenses = true;
        this.notifyFinancialDigest = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public MovementKind getDefaultMovementKind() {
        return defaultMovementKind;
    }

    public void setDefaultMovementKind(MovementKind defaultMovementKind) {
        this.defaultMovementKind = defaultMovementKind;
    }

    public Integer getDefaultAccountId() {
        return defaultAccountId;
    }

    public void setDefaultAccountId(Integer defaultAccountId) {
        this.defaultAccountId = defaultAccountId;
    }

    public Integer getDefaultTargetAccountId() {
        return defaultTargetAccountId;
    }

    public void setDefaultTargetAccountId(Integer defaultTargetAccountId) {
        this.defaultTargetAccountId = defaultTargetAccountId;
    }

    public Integer getDefaultCardId() {
        return defaultCardId;
    }

    public void setDefaultCardId(Integer defaultCardId) {
        this.defaultCardId = defaultCardId;
    }

    public Integer getDefaultExpenseCategoryId() {
        return defaultExpenseCategoryId;
    }

    public void setDefaultExpenseCategoryId(Integer defaultExpenseCategoryId) {
        this.defaultExpenseCategoryId = defaultExpenseCategoryId;
    }

    public Integer getDefaultIncomeCategoryId() {
        return defaultIncomeCategoryId;
    }

    public void setDefaultIncomeCategoryId(Integer defaultIncomeCategoryId) {
        this.defaultIncomeCategoryId = defaultIncomeCategoryId;
    }

    public Integer getDefaultInstallmentCount() {
        return defaultInstallmentCount;
    }

    public void setDefaultInstallmentCount(Integer defaultInstallmentCount) {
        this.defaultInstallmentCount = defaultInstallmentCount;
    }

    public Boolean getNotifyConnectionRequests() {
        return notifyConnectionRequests;
    }

    public boolean isNotifyConnectionRequests() {
        return !Boolean.FALSE.equals(notifyConnectionRequests);
    }

    public void setNotifyConnectionRequests(Boolean notifyConnectionRequests) {
        this.notifyConnectionRequests = notifyConnectionRequests;
    }

    public Boolean getNotifySharedExpenses() {
        return notifySharedExpenses;
    }

    public boolean isNotifySharedExpenses() {
        return !Boolean.FALSE.equals(notifySharedExpenses);
    }

    public void setNotifySharedExpenses(Boolean notifySharedExpenses) {
        this.notifySharedExpenses = notifySharedExpenses;
    }

    public Boolean getNotifyFinancialDigest() {
        return notifyFinancialDigest;
    }

    public boolean isNotifyFinancialDigest() {
        return !Boolean.FALSE.equals(notifyFinancialDigest);
    }

    public void setNotifyFinancialDigest(Boolean notifyFinancialDigest) {
        this.notifyFinancialDigest = notifyFinancialDigest;
    }
}
