package com.moviebooking.repository;

import com.moviebooking.entity.FoodCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodComboRepository extends JpaRepository<FoodCombo, Long> {
    List<FoodCombo> findByStatusOrderByIdAsc(String status);
}
