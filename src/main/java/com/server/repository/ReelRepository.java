package com.server.repository;

import com.server.entity.Reel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReelRepository extends JpaRepository<Reel, String> {

    public List<Reel> findAllByFunnyMoment(String funnyMoment);
}
