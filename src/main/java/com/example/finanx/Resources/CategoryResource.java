package com.example.finanx.Resources;

import com.example.finanx.DTO.CategoryRecord;
import com.example.finanx.Entities.Category;
import com.example.finanx.Services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryResource {
    private final CategoryService service;

    public CategoryResource(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoryRecord>> findAll() {
        List<CategoryRecord> categories = service.findAllActiveForAuthenticatedUser().stream()
                .map(CategoryRecord::new)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryRecord> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(new CategoryRecord(service.findAuthenticatedUserCategory(id)));
    }

    @PostMapping
    public ResponseEntity<CategoryRecord> create(@RequestBody CategoryRecord record) {
        Category category = service.create(record);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(category.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new CategoryRecord(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryRecord> update(@PathVariable Integer id, @RequestBody CategoryRecord record) {
        return ResponseEntity.ok(new CategoryRecord(service.update(id, record)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
