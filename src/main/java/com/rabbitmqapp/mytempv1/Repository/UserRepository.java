package com.rabbitmqapp.mytempv1.Repository;

import com.rabbitmqapp.mytempv1.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // Add this method
    boolean existsByEmail(String email);
    boolean existsByUsername(String username); // New method added
}
