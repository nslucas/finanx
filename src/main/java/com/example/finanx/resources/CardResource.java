package com.example.finanx.resources;

import com.example.finanx.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
public class CardResource {
    @Autowired
    private CardService service;

}
