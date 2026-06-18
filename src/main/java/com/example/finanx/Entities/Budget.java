package com.example.finanx.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "budget")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer categoryId;
    @Column(name = "budget_month")
    private Integer month;
    @Column(name = "budget_year")
    private Integer year;
    private BigDecimal amount;
    private Boolean active;
    private Integer userId;

    public Budget() {
    }

    public Budget(Integer id, Integer categoryId, Integer month, Integer year, BigDecimal amount,
                  Boolean active, Integer userId) {
        this.id = id;
        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.active = active;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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
