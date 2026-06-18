package com.example.finanx.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name="wallet")

public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String owner;
    private BigDecimal balance;
    @Transient
    private List<Card> cards;
    private Integer userId;
    
    public Wallet(String owner, BigDecimal balance, List<Card> cards, Integer userId) {
        this.owner = owner;
        this.balance = balance;
        this.cards = cards;
        this.userId = userId;
    }

    public Wallet() {

    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    public Integer getUserId() {
        return userId;
    }

    public void addBalance(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(value);
        } else {
            throw new IllegalArgumentException("The input value must be positive.");
        }
    }

    public void removeBalance(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            if (value.compareTo(this.balance) <= 0) {
                this.balance = this.balance.subtract(value);
            } else {
                throw new IllegalArgumentException("Insufficient balance for the operation!");
            }
        } else {
            throw new IllegalArgumentException("Value must be positive");
        }
    }

    public Integer getId() {
        return id;
    }


}
