package com.example.finanx.DTO;
import java.util.List;

import com.example.finanx.Entities.Card;
import com.example.finanx.Entities.Wallet;

public record WalletRecord(String owner, Double balance, List<Card> cards, Integer userId){
  public WalletRecord(Wallet wallet) {
        this(wallet.getOwner(), wallet.getBalance(), wallet.getCards(), wallet.getUserId());
  }
}
