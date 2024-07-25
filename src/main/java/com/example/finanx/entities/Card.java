package com.example.finanx.entities;

import jakarta.persistence.*;

@Entity
@Table(name= "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String number;
    private String owner;
    private Double creditLimit;
    private Double balance;

    private Long userId;

    public Card() {
    }

    public Card(String id, String number, String owner, Double creditLimit, Double balance, Long userId) {
        this.id = id;
        this.number = number;
        this.owner = owner;
        this.creditLimit = creditLimit;
        this.balance = balance;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Long getUserId() {
        return userId;
    }
}
