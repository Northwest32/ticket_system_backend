package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.Follow;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FollowMapper {
    
    @Insert("INSERT INTO follow (user_id, organizer_id) VALUES (#{userId}, #{organizerId})")
    int insertFollow(Follow follow);
    
    @Delete("DELETE FROM follow WHERE user_id = #{userId} AND organizer_id = #{organizerId}")
    int deleteFollow(@Param("userId") Long userId, @Param("organizerId") Long organizerId);
    
    @Select("SELECT f.*, u.username as organizerName, " +
            "(SELECT COUNT(*) FROM event WHERE organizer_id = f.organizer_id) as organizerEventCount, " +
            "(SELECT COUNT(*) FROM follow WHERE organizer_id = f.organizer_id) as followerCount " +
            "FROM follow f " +
            "LEFT JOIN users u ON f.organizer_id = u.id " +
            "WHERE f.user_id = #{userId}")
    List<Follow> findByUserIdWithDetails(Long userId);
    
    @Select("SELECT * FROM follow WHERE user_id = #{userId}")
    List<Follow> findByUserId(Long userId);
    
    @Select("SELECT * FROM follow WHERE user_id = #{userId} AND organizer_id = #{organizerId}")
    Follow findByUserIdAndOrganizerId(@Param("userId") Long userId, @Param("organizerId") Long organizerId);
    
    @Select("SELECT COUNT(*) FROM follow WHERE organizer_id = #{organizerId}")
    int countByOrganizerId(Long organizerId);
} 