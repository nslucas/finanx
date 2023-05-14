package com.example.finanx.resources;

import com.example.finanx.entities.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResource {
    @GetMapping
    public List<User>findAll() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        User lucas = new User("Lucas", "Nunes Santos", sdf.parse("30/09/1999"), "lucasn.nunes066@gmail.com", "lucas123");
        User arthur = new User("Arthur", "Nunes Santos", sdf.parse("06/10/2006"), "arthurn.nunes20@gmail.com", "arthur123");
        List<User> list = new ArrayList<>(Arrays.asList(lucas, arthur));
        return list;
    }
}
