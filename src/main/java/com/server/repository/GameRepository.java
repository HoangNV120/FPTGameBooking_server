package com.server.repository;

import com.server.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {

    Optional<Game> findByCode(String code);
}
