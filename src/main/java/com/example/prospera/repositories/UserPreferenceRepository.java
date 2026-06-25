package com.example.prospera.repositories;

import com.example.prospera.Entities.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    Optional<UserPreference> findByUserId(Integer userId);
}
