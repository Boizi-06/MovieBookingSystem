package com.moviebooking.repository;

import com.moviebooking.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByCinemaId(Long cinemaId);
    List<Room> findByCinemaId(Long cinemaId);
    Page<Room> findByCinemaId(Long cinemaId, Pageable pageable);
    boolean existsByNameAndCinemaId(String name, Long cinemaId);
    boolean existsByNameAndCinemaIdAndIdNot(String name, Long cinemaId, Long id);
}
