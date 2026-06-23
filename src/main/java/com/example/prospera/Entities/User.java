package com.example.prospera.Entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String lastName;
    private BigDecimal monthLimit;
    private String email;
    private String password;
    private String connectionCode;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Transient
    private List<Expense> expenses;


    public User() {
    }

    public User(String name, String lastName, BigDecimal monthLimit, String email) {
        this.name = name;
        this.lastName = lastName;
        this.monthLimit = monthLimit;
        this.email = email;
    }


    public User(Integer id, String name, String lastName, BigDecimal monthLimit, String email) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.monthLimit = monthLimit;
        this.email = email;
    }

    public User(Integer id, String name, String lastName, BigDecimal monthLimit, String email, String password) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.monthLimit = monthLimit;
        this.email = email;
        this.password = password;
    }

    public User(String name, String lastName, BigDecimal monthLimit, String email, String password, UserRole role) {
        this.name = name;
        this.lastName = lastName;
        this.monthLimit = monthLimit;
        this.email = email;
        this.password = password;
        this.role = role;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
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

    public BigDecimal getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(BigDecimal monthLimit) {
        this.monthLimit = monthLimit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password){ this.password = password; }

    public String getConnectionCode() {
        return connectionCode;
    }

    public void setConnectionCode(String connectionCode) {
        this.connectionCode = connectionCode;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

