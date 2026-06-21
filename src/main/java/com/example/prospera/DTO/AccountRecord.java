package com.example.prospera.DTO;

import com.example.prospera.Entities.Account;
import com.example.prospera.Entities.AccountType;

import java.math.BigDecimal;

public record AccountRecord(Integer id, String name, AccountType type, BigDecimal balance, String currency, Boolean active) {
    public AccountRecord(Account account) {
        this(account.getId(), account.getName(), account.getType(), account.getBalance(),
                account.getCurrency(), account.getActive());
    }
}
