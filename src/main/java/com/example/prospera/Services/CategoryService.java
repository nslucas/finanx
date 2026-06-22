package com.example.prospera.Services;

import com.example.prospera.DTO.CategoryRecord;
import com.example.prospera.Entities.Category;
import com.example.prospera.Entities.CategoryType;
import com.example.prospera.Entities.User;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public CategoryService(CategoryRepository categoryRepository, AuthenticatedUserService authenticatedUserService) {
        this.categoryRepository = categoryRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public List<Category> findAllActiveForAuthenticatedUser() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return categoryRepository.findByUserIdAndActiveTrueOrderByNameAsc(user.getId());
    }

    public Category findAuthenticatedUserCategory(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        return findUserCategory(user.getId(), id);
    }

    public Category findUserCategory(Integer userId, Integer categoryId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Category not found with id: " + categoryId));
    }

    public List<Category> findUserCategories(Integer userId, Collection<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return categoryRepository.findByUserIdAndIdIn(userId, categoryIds);
    }

    public Category requireActiveCategory(Integer userId, Integer categoryId, CategoryType type) {
        Category category = findUserCategory(userId, categoryId);
        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new IllegalArgumentException("Category is inactive");
        }
        if (type != null && category.getType() != type) {
            throw new IllegalArgumentException("Category type must be " + type);
        }
        return category;
    }

    public Category create(CategoryRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Category body is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        validate(record);
        validateUniqueName(user.getId(), null, record.name());

        Category category = new Category(null, normalizeName(record.name()), record.type(), true, user.getId());
        return categoryRepository.save(category);
    }

    public Category update(Integer id, CategoryRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Category body is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        validate(record);
        validateUniqueName(user.getId(), id, record.name());

        Category category = findUserCategory(user.getId(), id);
        category.setName(normalizeName(record.name()));
        category.setType(record.type());
        return categoryRepository.save(category);
    }

    public void deactivate(Integer id) {
        Category category = findAuthenticatedUserCategory(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private void validate(CategoryRecord record) {
        if (record.name() == null || record.name().isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
        if (record.type() == null) {
            throw new IllegalArgumentException("Category type is required");
        }
    }

    private void validateUniqueName(Integer userId, Integer currentId, String name) {
        categoryRepository.findByUserIdAndNameIgnoreCaseAndActiveTrue(userId, normalizeName(name))
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Active category name already exists");
                });
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}
