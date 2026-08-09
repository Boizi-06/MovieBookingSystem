package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.service.CloudinaryImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class ImageUploadController {

    private final CloudinaryImageService cloudinaryImageService;

    public ImageUploadController(CloudinaryImageService cloudinaryImageService) {
        this.cloudinaryImageService = cloudinaryImageService;
    }

    @PostMapping("/upload-poster")
    public ResponseEntity<ApiResponse<String>> uploadPoster(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.<String>builder()
                    .success(false)
                    .message("File is empty")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        try {
            String url = cloudinaryImageService.upload(file);
            return new ResponseEntity<>(ApiResponse.<String>builder()
                    .success(true)
                    .message("Poster uploaded successfully")
                    .data(url)
                    .build(), HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>(ApiResponse.<String>builder()
                    .success(false)
                    .message("Could not upload to Cloudinary: " + ex.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
