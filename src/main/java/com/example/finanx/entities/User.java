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

    public void setPassword(String password) {
        this.password = password;
    }

    /*public void addExpense(Expense expense) {
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        double totalExpenses = expenses.stream()
                .filter(e -> isSameMonth(e.getPurchaseDate()))
                .mapToDouble(Expense::getAmount)
                .sum();

        if (totalExpenses + expense.getAmount() > monthLimit) {
            throw new RuntimeException("Limite de despesas atingido para o mês.");
    }

     */
        /*private boolean isSameMonth(Date date) {
            Calendar currentMonth = Calendar.getInstance();
            Calendar targetMonth = Calendar.getInstance();
            targetMonth.setTime(date);

            int currentYear = currentMonth.get(Calendar.YEAR);
            int currentMonthValue = currentMonth.get(Calendar.MONTH);
            int targetYear = targetMonth.get(Calendar.YEAR);
            int targetMonthValue = targetMonth.get(Calendar.MONTH);

            return currentYear == targetYear && currentMonthValue == targetMonthValue;
        }

         */

    }

