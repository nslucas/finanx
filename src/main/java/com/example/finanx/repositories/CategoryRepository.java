package com.example.finanx.Repositories;

import com.example.finanx.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByUserIdAndActiveTrueOrderByNameAsc(Integer userId);

    Optional<Category> findByIdAndUserId(Integer id, Integer userId);

    Optional<Category> findByUserIdAndNameIgnoreCaseAndActiveTrue(Integer userId, String name);
}
