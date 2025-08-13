package com.ziye.ticket.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ziye.ticket.mapper.UserMapper;
import java.util.List;
import java.util.Map;

/**
 * 头像URL迁移工具类
 * 用于清理旧的本地头像路径，避免404错误
 */
@Component
public class AvatarMigrationUtil {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 清理旧的本地头像路径
     * 将 /uploads/avatars/ 开头的路径设置为null
     */
    public void cleanOldLocalAvatarPaths() {
        try {
            int affectedRows = userMapper.cleanOldLocalAvatarPaths();
            System.out.println("✅ 头像路径清理完成，影响了 " + affectedRows + " 条记录");
        } catch (Exception e) {
            System.err.println("❌ 头像路径清理失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户头像URL格式
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
     * 检查是否为旧的本地路径
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
