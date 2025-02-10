package com.server.repository;

import com.server.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    Optional<Room> findRoomByCode(String code);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Room U WHERE U.id = :id ")
    void deleteById(String id);
}
