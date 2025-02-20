package com.server.repository;

import com.server.entity.User;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRole(RoleEnum role);
    // Find users by name (partial search)
    Page<User> findByNameContaining(String name, Pageable pageable);

    // Find users by email (partial search)
    Page<User> findByEmailContaining(String email, Pageable pageable);

    Optional<User> findByStatus(StatusEnum status);

    Optional<User> findUsersByActiveToken(String token);

    Optional<User> findFirstByEmailOrName(String email, String name);
}
