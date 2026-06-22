package com.example.prospera.repositories;

import com.example.prospera.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByUserIdAndActiveTrueOrderByNameAsc(Integer userId);

    Optional<Category> findByIdAndUserId(Integer id, Integer userId);

    List<Category> findByUserIdAndIdIn(Integer userId, Collection<Integer> ids);

    Optional<Category> findByUserIdAndNameIgnoreCaseAndActiveTrue(Integer userId, String name);
}
