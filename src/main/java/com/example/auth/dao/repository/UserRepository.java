package com.example.auth.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.example.auth.dao.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNum(String phoneNum);

    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO USER VALUES (?1)", nativeQuery = true)
    void saveWithoutJoin(String userId);    
}
