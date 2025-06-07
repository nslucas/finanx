package com.example.finanx.Resources;

import com.example.finanx.DTO.UserRecord;
import com.example.finanx.Entities.User;
import com.example.finanx.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResource {

    @Autowired
    private UserService service;

    /*
    TO-DO: Make the method below return a custom message to the user explaining why not authorized.
     */
    @GetMapping
    public ResponseEntity<List<UserRecord>> findAll(){
        List<UserRecord> list = service.findAll().stream().map(UserRecord::new).toList();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<UserRecord> findById(@PathVariable Integer id) {
        User obj = service.findById(id);
        return ResponseEntity.ok().body(new UserRecord(obj));
    }


    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<User> update(@RequestBody UserRecord objDTO, @PathVariable Integer id) {
        User obj = service.fromDTO(objDTO);
        obj.setId(id);
        obj = service.update(obj);
        return ResponseEntity.noContent().build();
    }
}
