package com.server.repository;

import com.server.entity.Reel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReelRepository extends JpaRepository<Reel, String> {
    // JpaRepository đã hỗ trợ các thao tác cần thiết
}
