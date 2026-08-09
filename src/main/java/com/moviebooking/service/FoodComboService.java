package com.moviebooking.service;

import com.moviebooking.dto.FoodComboRequest;
import com.moviebooking.dto.FoodComboResponse;
import java.util.List;

public interface FoodComboService {
    List<FoodComboResponse> getActiveCombos();
    List<FoodComboResponse> getAllCombosAdmin();
    FoodComboResponse createCombo(FoodComboRequest request);
    FoodComboResponse updateCombo(Long id, FoodComboRequest request);
    void deleteCombo(Long id);
}
