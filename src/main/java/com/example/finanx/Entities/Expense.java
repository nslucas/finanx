package com.example.finanx.Entities;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private BigDecimal amount;
    private String name;
    private Integer installmentCount;
    private LocalDateTime purchaseDate;
    private String description;
    private Integer userId;
    private Integer cardId;

    public Expense(){}

    public Expense(Integer id, String name, BigDecimal amount, Integer installmentCount, LocalDateTime purchaseDate,
                   String description, Integer userId, Integer cardId) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.installmentCount = installmentCount;
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.userId = userId;
        this.cardId = cardId;
    }

    public Integer getId() {
        return id;
    }

    public void Integer(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInstallmentCount() {
        return installmentCount;
    }

    public void setInstallmentCount(Integer installmentCount) {
        this.installmentCount = installmentCount;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

}
