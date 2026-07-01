package com.yourapp.auth.repository;

import com.yourapp.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    
    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<UserEntity> findByIdAndEnabledTrue(String id);
}