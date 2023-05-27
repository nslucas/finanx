package com.example.finanx.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

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
    private Date purchaseDate;
    private String description;
    @ManyToOne
    @JoinColumn(name="userId")
    @JsonBackReference
    private User user;

    public Expense(){}

    public Expense(String id, Double amount, String name, Integer installmentCount, Date purchaseDate, String description, User user) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.installmentCount = installmentCount;
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.user = user;
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

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
