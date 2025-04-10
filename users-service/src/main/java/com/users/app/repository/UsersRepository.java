package com.users.app.repository;

import com.users.app.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByPhone(String phone);
    Optional<Users> findByEmail(String email); // kalau mau cek email juga
}
