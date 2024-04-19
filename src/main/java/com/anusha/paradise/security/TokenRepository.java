package com.anusha.paradise.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Query(value = "select t.* from token t inner join users u on t.user_id = u.id where u.id = :id and (t.expired = false or t.revoked = false)", nativeQuery = true)
    Optional<List<Token>> findAllValidTokenByUser(UUID id);
}