package com.ziye.ticket.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ziye.ticket.mapper.UserMapper;

@Component
public class AvatarMigrationUtil {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * Clean old local avatar paths
     * Set paths starting with /uploads/avatars/ to null
     */
    public void cleanOldLocalAvatarPaths() {
        try {
            int affectedRows = userMapper.cleanOldLocalAvatarPaths();
            System.out.println("✅ Avatar paths cleaned, affected " + affectedRows + " records");
        } catch (Exception e) {
            System.err.println("❌ Avatar paths cleaning failed: " + e.getMessage());
        }
    }
    
    /**
     * Check if the user avatar URL is valid
     * @param avatarUrl 头像URL
     * @return true if valid Cloudinary URL, false otherwise
     */
    public static boolean isValidCloudinaryUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return false;
        }
        return avatarUrl.startsWith("https://res.cloudinary.com/");
    }
    
    /**
     * Check if the avatar URL is an old local path
     * @param avatarUrl 头像URL
     * @return true if old local path, false otherwise
     */
    public static boolean isOldLocalPath(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return false;
        }
        return avatarUrl.startsWith("/uploads/avatars/");
    }
}
