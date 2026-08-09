package com.moviebooking.repository;

import com.moviebooking.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByTypeAndStatusOrderByIdDesc(String type, String status);
    List<Promotion> findByStatusOrderByIdDesc(String status);
}
