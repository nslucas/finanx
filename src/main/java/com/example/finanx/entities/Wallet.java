package com.example.finanx.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="wallet")

public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String owner;
    private Double balance;
    @Transient
    private List<Card> cards;
    private Long userId;
    
    public Wallet(String owner, Double balance, List<Card> cards, Long userId) {
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

    public Double getBalance() {
        return balance;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    public Long getUserId() {
        return userId;
    }

    public void addBalance(double value) {
        if (value > 0) {
            this.balance += value;
        } else {
            throw new IllegalArgumentException("The input value must be positive.");
        }
    }

    public void removeBalance(double value) {
        if (value > 0) {
            if (value <= this.balance) {
                this.balance -= value;
            } else {
                throw new IllegalArgumentException("Insufficient balance for the operation!");
            }
        } else {
            throw new IllegalArgumentException("Value must be positive");
        }
    }


}
