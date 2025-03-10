package com.example.finanx.Services;

import com.example.finanx.DTO.UserRecord;
import com.example.finanx.Entities.User;
import com.example.finanx.Exceptions.ObjectNotFoundException;
import com.example.finanx.Repositories.UserRepository;
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

    public User findById(Integer id) {
        Optional<User> optionalUser = repository.findById(id);
        return optionalUser.orElseThrow(() -> new ObjectNotFoundException("Object not found!"));
    }

    public User insert(User obj) {
        return repository.save(obj);
    }

    public void delete(Integer id){
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
        newObj.setMonthLimit(obj.getMonthLimit());
        newObj.setEmail(obj.getEmail());
    }

    public User fromDTO(UserRecord objDTO){
        return new User(objDTO.name(), objDTO.lastName(), objDTO.monthLimit(), objDTO.email());
    }

}
