
package com.example.finanx.configuration;


import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;
import com.example.finanx.repositories.ExpenseRepository;
import com.example.finanx.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

@Configuration
public class Instantiation implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExpenseRepository expenseRepository;

    @Override
    public void run(String... args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        expenseRepository.deleteAll();
        userRepository.deleteAll();

        User lucas = new User(null,"Lucas", "Nunes", 3500.0, "lucasnunes.santos@gmail.com", "Lu@25462933001!");
        User tais = new User(null, "Maria", "Rodrigues", 4000.0, "maria.rodrigues@hotmail.com", "tais123");
        User rodrigo = new User(null, "Rodrigo", "Babisque", 10000.0, "rodrigo.babisque@hotmail.com", "rodrigo321");
        userRepository.saveAll(Arrays.asList(lucas, tais, rodrigo));

        Expense expense1 = new Expense(null, 2000.0, "Curso Java - do Básico ao Avançado!", 5, sdf.parse("24/05/2023"), "curso pra aprender a fazer API", lucas.getId());
        Expense expense2 = new Expense(null, 200.0, "Jantar de aniversário de namoro", 1, sdf.parse("07/07/2023"), "jantar especial", tais.getId());
        Expense expense3 = new Expense(null, 70000.0, "Focus 2018", 1, sdf.parse("25/05/2023"), "carro novo", rodrigo.getId());
        Expense expense4 = new Expense(null, 5000.0, "Pc gamer", 3, sdf.parse("28/05/2023"), "pc para jogar com os amigos e estudar", rodrigo.getId());
        expenseRepository.saveAll(Arrays.asList(expense1, expense2, expense3));

    }
}

