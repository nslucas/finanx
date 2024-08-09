package com.example.finanx.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private Double monthLimit;
    private String email;
    private String password;
    @OneToMany(mappedBy = "userId")
    @JsonManagedReference
    private List<Expense> expenses;


    public User() {
    }

    public User(Long id, String name, String lastName, Double monthLimit, String email) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.monthLimit = monthLimit;
        this.email = email;
    }

    public User(Long id, String name, String lastName, Double monthLimit, String email, String password) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.monthLimit = monthLimit;
        this.email = email;
        this.password = password;
    }

    public User(String name, String lastName, Double monthLimit, String email) {
        this.name = name;
        this.lastName = lastName;
        this.monthLimit = monthLimit;
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(Double monthLimit) {
        this.monthLimit = monthLimit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){ this.password = password; }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

}

