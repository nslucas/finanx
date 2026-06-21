package com.example.prospera.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name= "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String bankName;
    private String name;
    private String network;
    private String lastFourDigits;
    private BigDecimal creditLimit;
    private Integer closingDay;
    private Integer dueDay;
    private Boolean active = true;
    private Integer userId;

    public Card() {
    }

    public Card(Integer id, String bankName, String name, String network, String lastFourDigits,
                BigDecimal creditLimit, Integer closingDay, Integer dueDay, Boolean active, Integer userId) {
        this.id = id;
        this.bankName = bankName;
        this.name = name;
        this.network = network;
        this.lastFourDigits = lastFourDigits;
        this.creditLimit = creditLimit;
        this.closingDay = closingDay;
        this.dueDay = dueDay;
        this.active = active == null || active;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getClosingDay() {
        return closingDay;
    }

    public void setClosingDay(Integer closingDay) {
        this.closingDay = closingDay;
    }

    public Integer getDueDay() {
        return dueDay;
    }

    public void setDueDay(Integer dueDay) {
        this.dueDay = dueDay;
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
