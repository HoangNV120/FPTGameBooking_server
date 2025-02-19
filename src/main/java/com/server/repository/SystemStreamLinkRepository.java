package com.server.repository;

import com.server.entity.SystemStreamLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemStreamLinkRepository extends JpaRepository<SystemStreamLink, String> {

    @Query("SELECT s FROM SystemStreamLink s ORDER BY s.createdDate DESC LIMIT 1")
    SystemStreamLink findLatestStreamLink();
}
