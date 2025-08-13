package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE username = #{username}")
    @Results({
        @Result(property = "userType", column = "user_type"),
        @Result(property = "createdAt", column = "created_at")
    })
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    @Results({
        @Result(property = "userType", column = "user_type"),
        @Result(property = "createdAt", column = "created_at")
    })
    User findByEmail(String email);

    @Insert("INSERT INTO users (username, password, email, user_type, created_at) VALUES (#{username}, #{password}, #{email}, #{userType}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    @Update("UPDATE users SET password = #{password} WHERE username = #{username}")
    int updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

    @Update("UPDATE users SET password = #{password} WHERE email = #{email}")
    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results({
        @Result(property = "userType", column = "user_type"),
        @Result(property = "createdAt", column = "created_at")
    })
    User findById(Long id);

    @Select("SELECT * FROM users WHERE email = #{email} AND username = #{username}")
    @Results({
        @Result(property = "userType", column = "user_type"),
        @Result(property = "createdAt", column = "created_at")
    })
    User findByEmailAndUsername(@Param("email") String email, @Param("username") String username);
    
    @Update("UPDATE users SET avatar_url = #{avatarUrl} WHERE id = #{id}")
    int updateAvatarUrl(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);
    
    @Update("UPDATE users SET avatar_url = NULL WHERE avatar_url LIKE '/uploads/avatars/%'")
    int cleanOldLocalAvatarPaths();
}
