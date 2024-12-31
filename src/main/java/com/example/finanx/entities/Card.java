package com.example.finanx.entities;

import jakarta.persistence.*;

@Entity
@Table(name= "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String number;
    private String owner;
    private Double creditLimit;
    private Double balance;

    private Integer userId;

    public Card() {
    }

    public Card(Integer id, String number, String owner, Double creditLimit, Double balance, Integer userId) {
        this.id = id;
        this.number = number;
        this.owner = owner;
        this.creditLimit = creditLimit;
        this.balance = balance;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getUserId() {
        return userId;
    }
}
