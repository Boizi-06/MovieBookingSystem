package com.moviebooking.service;

import com.moviebooking.dto.PromotionRequest;
import com.moviebooking.dto.PromotionResponse;
import java.util.List;

public interface PromotionService {
    List<PromotionResponse> getPromotionsByType(String type);
    List<PromotionResponse> getAllPromotionsAdmin();
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    void deletePromotion(Long id);
}
