package com.moviebooking.service.impl;

import com.moviebooking.dto.PromotionRequest;
import com.moviebooking.dto.PromotionResponse;
import com.moviebooking.entity.Promotion;
import com.moviebooking.repository.PromotionRepository;
import com.moviebooking.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Autowired
    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getPromotionsByType(String type) {
        List<Promotion> list;
        if (type != null && !type.trim().isEmpty()) {
            list = promotionRepository.findByTypeAndStatusOrderByIdDesc(type.trim().toUpperCase(), "ACTIVE");
        } else {
            list = promotionRepository.findByStatusOrderByIdDesc("ACTIVE");
        }
        return list.stream().map(PromotionResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotionsAdmin() {
        return promotionRepository.findAll().stream()
                .map(PromotionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        Promotion promotion = Promotion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .type(request.getType() != null ? request.getType().toUpperCase() : "PROMOTION")
                .status(request.getStatus() != null ? request.getStatus().toUpperCase() : "ACTIVE")
                .build();
        Promotion saved = promotionRepository.save(promotion);
        return PromotionResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin Khuyến mãi với ID: " + id));

        promotion.setTitle(request.getTitle());
        promotion.setDescription(request.getDescription());
        promotion.setImageUrl(request.getImageUrl());
        promotion.setLinkUrl(request.getLinkUrl());
        if (request.getType() != null) promotion.setType(request.getType().toUpperCase());
        if (request.getStatus() != null) promotion.setStatus(request.getStatus().toUpperCase());

        Promotion updated = promotionRepository.save(promotion);
        return PromotionResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy thông tin Khuyến mãi với ID: " + id);
        }
        promotionRepository.deleteById(id);
    }
}
