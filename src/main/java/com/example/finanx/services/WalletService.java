package com.example.finanx.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finanx.dto.WalletRecord;
import com.example.finanx.entities.Wallet;
import com.example.finanx.exception.ObjectNotFoundException;
import com.example.finanx.repositories.WalletRepository;

@Service
public class WalletService {
      @Autowired
      private final WalletRepository repository;

      public WalletService(WalletRepository repository) {
        this.repository = repository;
      }

      public List<Wallet> findAll() {
        return repository.findAll();
    }

    public Wallet findById(Long id) {
        Optional<Wallet> optionalWallet = repository.findById(id);
        return optionalWallet.orElseThrow(() -> new ObjectNotFoundException("Object not found!"));
    }
    	public Wallet insert(Wallet obj) {
        return repository.save(obj);
    }

    public void delete(Long id){
        findById(id);
        repository.deleteById(id);
    }

    public Wallet update(Wallet obj){
        Wallet newObj = repository.getReferenceById(obj.getId());
        updateData(newObj, obj);
        return repository.save(newObj);
    }

    private void updateData(Wallet newObj, Wallet obj) {
        newObj.setOwner(obj.getOwner());
        newObj.setCards(obj.getCards());
    }

    public Wallet fromDTO(WalletRecord objDTO){
        return new Wallet(objDTO.owner(), objDTO.balance(), objDTO.cards(), objDTO.userId());
    }
}
