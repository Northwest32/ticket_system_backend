package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.Bookmark;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BookmarkMapper {
    
    @Insert("INSERT INTO bookmark (user_id, event_id) VALUES (#{userId}, #{eventId})")
    int insertBookmark(Bookmark bookmark);
    
    @Delete("DELETE FROM bookmark WHERE user_id = #{userId} AND event_id = #{eventId}")
    int deleteBookmark(@Param("userId") Long userId, @Param("eventId") Long eventId);
    
    @Select("SELECT * FROM bookmark WHERE user_id = #{userId}")
    List<Bookmark> findByUserId(Long userId);
    
    @Select("SELECT * FROM bookmark WHERE user_id = #{userId} AND event_id = #{eventId}")
    Bookmark findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);
    
    @Select("SELECT COUNT(*) FROM bookmark WHERE event_id = #{eventId}")
    int countByEventId(Long eventId);
} 