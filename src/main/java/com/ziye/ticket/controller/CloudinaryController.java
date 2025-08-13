package com.ziye.ticket.controller;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cloudinary")
public class CloudinaryController {
    
    @Autowired 
    private Cloudinary cloudinary;

    @GetMapping("/signature")
    public Map<String, Object> signature(
            @RequestParam(defaultValue = "ticket-system/uploads") String folder,
            @RequestParam(required = false) String publicId 
    ) {
        long timestamp = System.currentTimeMillis() / 1000;
        Map<String, Object> toSign = new HashMap<>();
        toSign.put("timestamp", timestamp);
        toSign.put("folder", folder);
        if (publicId != null && !publicId.isBlank()) {
            toSign.put("public_id", publicId);
        }
        String sig = cloudinary.apiSignRequest(toSign, cloudinary.config.apiSecret);

        Map<String, Object> resp = new HashMap<>();
        resp.put("timestamp", timestamp);
        resp.put("signature", sig);
        resp.put("apiKey", cloudinary.config.apiKey);
        resp.put("cloudName", cloudinary.config.cloudName);
        resp.put("folder", folder);
        if (publicId != null) resp.put("publicId", publicId);
        return resp;
    }
}
