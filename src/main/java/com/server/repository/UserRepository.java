package com.server.repository;

import com.server.entity.User;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRole(RoleEnum role);

    Optional<User> findByStatus(StatusEnum status);

    Optional<User> findUsersByActiveToken(String token);
}
