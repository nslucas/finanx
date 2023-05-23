package com.example.finanx.services;

import com.example.finanx.dto.UserRecord;
import com.example.finanx.entities.User;
import com.example.finanx.exception.ObjectNotFoundException;
import com.example.finanx.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        Optional<User> optionalUser = repository.findById(id);
        return optionalUser.orElseThrow(() -> new ObjectNotFoundException("Object not found!"));
    }

    public User insert(User obj) {
        return repository.save(obj);
    }

    public void delete(Long id){
        findById(id);
        repository.deleteById(id);
    }

    public User update(User obj){
        User newObj = repository.getReferenceById(obj.getId());
        updateData(newObj, obj);
        return repository.save(newObj);
    }

    private void updateData(User newObj, User obj) {
        newObj.setName(obj.getName());
        newObj.setLastName(obj.getLastName());
        newObj.setEmail(obj.getEmail());
    }

    public User fromDTO(UserRecord objDTO){
        return new User(objDTO.id(), objDTO.name(), objDTO.lastName(), objDTO.monthLimit(), objDTO.email(), objDTO.password());
    }

}
