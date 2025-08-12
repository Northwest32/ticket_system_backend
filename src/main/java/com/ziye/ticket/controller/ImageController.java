package com.ziye.ticket.controller;

import com.ziye.ticket.dto.CommonResponse;
import com.ziye.ticket.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/upload-event-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<String> uploadEventImage(@RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new CommonResponse<>(1, "Please select a file to upload", null);
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return new CommonResponse<>(1, "Only image files are allowed", null);
            }

            // Validate file size (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return new CommonResponse<>(1, "File size should be less than 5MB", null);
            }

            String imageUrl = imageService.uploadEventImage(file);
            return new CommonResponse<>(0, "Image uploaded successfully", imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResponse<>(1, "Failed to upload image: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/delete-image")
    public CommonResponse<Boolean> deleteImage(@RequestParam("publicId") String publicId) {
        try {
            boolean deleted = imageService.deleteImage(publicId);
            if (deleted) {
                return new CommonResponse<>(0, "Image deleted successfully", true);
            } else {
                return new CommonResponse<>(1, "Failed to delete image", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResponse<>(1, "Failed to delete image: " + e.getMessage(), false);
        }
    }
} 