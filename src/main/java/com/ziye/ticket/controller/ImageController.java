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

    @GetMapping("/test-image-access")
    public CommonResponse<String> testImageAccess() {
        try {
            // Check if upload directory exists
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/event-images/");
            boolean directoryExists = java.nio.file.Files.exists(uploadPath);
            
            // List files in directory
            String files = "";
            if (directoryExists) {
                files = java.nio.file.Files.list(uploadPath)
                    .map(path -> path.getFileName().toString())
                    .collect(java.util.stream.Collectors.joining(", "));
            }
            
            String result = String.format("Directory exists: %s, Files: %s", directoryExists, files);
            return new CommonResponse<>(0, "Image access test", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResponse<>(1, "Test failed: " + e.getMessage(), null);
        }
    }
} 