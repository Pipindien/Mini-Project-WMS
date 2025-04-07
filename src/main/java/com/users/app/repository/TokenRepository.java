package com.users.app.repository;

import com.users.app.entity.Token;
import com.users.app.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(Users user);
    Optional<Token> findByJwtToken(String jwtToken);
}
