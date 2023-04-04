package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail (String email);
    Optional<User> findByUsername (String username);
//    Optional<User> findByCompanyName (String companyName);

}
