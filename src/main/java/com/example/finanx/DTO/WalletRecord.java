package com.example.finanx.DTO;
import java.math.BigDecimal;
import java.util.List;

import com.example.finanx.Entities.Card;
import com.example.finanx.Entities.Wallet;

public record WalletRecord(String owner, BigDecimal balance, List<Card> cards, Integer userId){
  public WalletRecord(Wallet wallet) {
        this(wallet.getOwner(), wallet.getBalance(), wallet.getCards(), wallet.getUserId());
  }
}
