package com.example.finanx.entities;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Double amount;
    private String name;
    private Integer installmentCount;
    private LocalDateTime purchaseDate;
    private String description;
    private Long userId;

    public Expense(){}

    public Expense(String id, String name, Double amount, Integer installmentCount, LocalDateTime purchaseDate, String description, Long userId) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.installmentCount = installmentCount;
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void String(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
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

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

}
