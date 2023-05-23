package com.example.finanx.resources;

import com.example.finanx.dto.UserRecord;
import com.example.finanx.entities.User;
import com.example.finanx.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserResource {

    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<List<UserRecord>> findAll(){
        List<UserRecord> list = service.findAll().stream().map(UserRecord::new).toList();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<UserRecord> findById(@PathVariable Long id) {
        User obj = service.findById(id);
        return ResponseEntity.ok().body(new UserRecord(obj));
    }

    @PostMapping
        public ResponseEntity<Void> insert(@RequestBody UserRecord objDTO) {
        User obj = service.fromDTO(objDTO);
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<Void> update(@RequestBody UserRecord objDTO, @PathVariable Long id) {
        User obj = service.fromDTO(objDTO);
        obj.setId(id);
        obj = service.update(obj);
        return ResponseEntity.noContent().build();
    }
}
