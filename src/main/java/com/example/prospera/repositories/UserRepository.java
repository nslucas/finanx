package com.example.prospera.repositories;

import com.example.prospera.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByConnectionCode(String connectionCode);

    boolean existsByConnectionCode(String connectionCode);
}
