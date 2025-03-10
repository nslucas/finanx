package com.example.finanx.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finanx.DTO.WalletRecord;
import com.example.finanx.Entities.Wallet;
import com.example.finanx.Exceptions.ObjectNotFoundException;
import com.example.finanx.Repositories.WalletRepository;

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

    public Wallet findById(Integer id) {
        Optional<Wallet> optionalWallet = repository.findById(id);
        return optionalWallet.orElseThrow(() -> new ObjectNotFoundException("Object not found!"));
    }
    	public Wallet insert(Wallet obj) {
        return repository.save(obj);
    }

    public void delete(Integer id){
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
