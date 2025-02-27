package com.server.repository;

import com.server.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {

    Optional<Team> findByName(String name);

    List<Team> findByRoom_code(String code);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Team U WHERE U.id = :id ")
    void deleteById(String id);

}
