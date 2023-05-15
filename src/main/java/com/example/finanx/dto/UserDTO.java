package com.example.finanx.dto;

import com.example.finanx.entities.User;

import java.io.Serializable;
import java.util.Optional;

public class UserDTO implements Serializable {


    private Long id;
    private String name;
    private String lastName;

    private String email;

    public UserDTO(Optional<User> obj) {
    }

    public UserDTO(User obj){
        id = obj.getId();
        name = obj.getName();
        lastName = obj.getLastName();
        email = obj.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }
}
