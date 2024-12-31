package com.example.finanx.dto;
import java.util.List;

import com.example.finanx.entities.Card;
import com.example.finanx.entities.Wallet;

public record WalletRecord(String owner, Double balance, List<Card> cards, Integer userId){
  public WalletRecord(Wallet wallet) {
        this(wallet.getOwner(), wallet.getBalance(), wallet.getCards(), wallet.getUserId());
  }
}
