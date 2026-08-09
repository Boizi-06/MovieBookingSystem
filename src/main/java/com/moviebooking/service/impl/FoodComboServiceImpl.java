package com.moviebooking.service.impl;

import com.moviebooking.dto.FoodComboRequest;
import com.moviebooking.dto.FoodComboResponse;
import com.moviebooking.entity.FoodCombo;
import com.moviebooking.repository.FoodComboRepository;
import com.moviebooking.service.FoodComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodComboServiceImpl implements FoodComboService {

    private final FoodComboRepository foodComboRepository;

    @Autowired
    public FoodComboServiceImpl(FoodComboRepository foodComboRepository) {
        this.foodComboRepository = foodComboRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodComboResponse> getActiveCombos() {
        return foodComboRepository.findByStatusOrderByIdAsc("ACTIVE").stream()
                .map(FoodComboResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodComboResponse> getAllCombosAdmin() {
        return foodComboRepository.findAll().stream()
                .map(FoodComboResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoodComboResponse createCombo(FoodComboRequest request) {
        FoodCombo combo = FoodCombo.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus() != null ? request.getStatus().toUpperCase() : "ACTIVE")
                .build();
        FoodCombo saved = foodComboRepository.save(combo);
        return FoodComboResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public FoodComboResponse updateCombo(Long id, FoodComboRequest request) {
        FoodCombo combo = foodComboRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Combo bỏng nước với ID: " + id));

        combo.setName(request.getName());
        combo.setDescription(request.getDescription());
        combo.setPrice(request.getPrice());
        combo.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) combo.setStatus(request.getStatus().toUpperCase());

        FoodCombo updated = foodComboRepository.save(combo);
        return FoodComboResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteCombo(Long id) {
        if (!foodComboRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy Combo bỏng nước với ID: " + id);
        }
        foodComboRepository.deleteById(id);
    }
}
