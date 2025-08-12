package com.ziye.ticket.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private final Cloudinary cloudinary;

    @Autowired
    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload image to Cloudinary
     * @param file The image file to upload
     * @return The public URL of the uploaded image
     * @throws IOException if upload fails
     */
    public String uploadEventImage(MultipartFile file) throws IOException {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "event-images",
                    "resource_type", "auto"
                )
            );
            
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new IOException("Failed to upload image to Cloudinary", e);
        }
    }

    /**
     * Delete image from Cloudinary
     * @param publicId The public ID of the image to delete
     * @return true if deletion was successful
     */
    public boolean deleteImage(String publicId) {
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            return false;
        }
    }
} 