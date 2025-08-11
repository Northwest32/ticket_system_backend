package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.Event;
import org.apache.ibatis.annotations.*;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;

@Mapper
public interface EventMapper {
    @Insert("INSERT INTO event (title, description, image_url, location, category_id, event_date, price, capacity, remaining_quantity, status, created_by, created_at, start_time, end_time) VALUES (#{title}, #{description}, #{imageUrl}, #{location}, #{categoryId}, #{eventDate}, #{price}, #{capacity}, #{remainingQuantity}, #{status}, #{createdBy}, #{createdAt}, #{startTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertEvent(Event event);

    @Select("<script>SELECT e.*, c.name as categoryName, u.username as organizerName, u.username as organizerUsername, u.avatar_url as organizerAvatarUrl, op.homepage_description as organizerDescription FROM event e LEFT JOIN category c ON e.category_id = c.id LEFT JOIN users u ON e.created_by = u.id LEFT JOIN organizer_profile op ON e.created_by = op.organizer_id WHERE e.status != 'draft' <if test='categoryId!=null'> AND e.category_id=#{categoryId}</if><if test='keyword!=null'> AND (LOWER(e.title) LIKE CONCAT('%',LOWER(#{keyword}),'%') OR LOWER(c.name) LIKE CONCAT('%',LOWER(#{keyword}),'%') OR LOWER(u.username) LIKE CONCAT('%',LOWER(#{keyword}),'%') OR LOWER(op.homepage_description) LIKE CONCAT('%',LOWER(#{keyword}),'%'))</if> ORDER BY e.event_date DESC</script>")
    List<Event> searchEvents(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Select("SELECT e.*, c.name as categoryName, u.username as organizerName, u.username as organizerUsername, u.avatar_url as organizerAvatarUrl, op.homepage_description as organizerDescription FROM event e LEFT JOIN category c ON e.category_id = c.id LEFT JOIN users u ON e.created_by = u.id LEFT JOIN organizer_profile op ON e.created_by = op.organizer_id ORDER BY e.event_date DESC")
    List<Event> findAll();

    @Select("SELECT e.*, c.name as categoryName, u.username as organizerName, u.username as organizerUsername, u.avatar_url as organizerAvatarUrl, op.homepage_description as organizerDescription FROM event e LEFT JOIN category c ON e.category_id = c.id LEFT JOIN users u ON e.created_by = u.id LEFT JOIN organizer_profile op ON e.created_by = op.organizer_id WHERE e.id = #{id}")
    Event findById(Long id);

    @Select("SELECT e.*, c.name as categoryName, u.username as organizerName, u.username as organizerUsername, u.avatar_url as organizerAvatarUrl FROM event e LEFT JOIN category c ON e.category_id = c.id LEFT JOIN users u ON e.created_by = u.id ORDER BY e.event_date DESC LIMIT #{size} OFFSET #{offset}")
    List<Event> findByPage(@Param("offset") int offset, @Param("size") int size);
    
    @Select("SELECT e.*, c.name as categoryName, u.username as organizerName, u.username as organizerUsername, u.avatar_url as organizerAvatarUrl FROM event e LEFT JOIN category c ON e.category_id = c.id LEFT JOIN users u ON e.created_by = u.id WHERE e.created_by = #{organizerId} ORDER BY e.event_date DESC")
    List<Event> findByOrganizerId(Long organizerId);

    @Select("SELECT e.*, c.name as categoryName, u.username as organizerName, u.username as organizerUsername, u.avatar_url as organizerAvatarUrl FROM event e LEFT JOIN category c ON e.category_id = c.id LEFT JOIN users u ON e.created_by = u.id WHERE e.end_time <= #{now} AND e.status != 'finished'")
    List<Event> findEventsEnded(@Param("now") LocalDateTime now);

    @Update("UPDATE event SET status = 'finished' WHERE id = #{id}")
    int markFinished(@Param("id") Long id);

    @Update("UPDATE event SET capacity = #{remain} WHERE id = #{id}")
    int resetRemain(@Param("id") Long id, @Param("remain") int remain);

    @Update("UPDATE event SET remaining_quantity = #{remain} WHERE id = #{id}")
    int updateRemainingQuantity(@Param("id") Long id, @Param("remain") int remain);

    @Select("SELECT e.*, c.name as categoryName, u.username as organizerName, u.username as organizerUsername, u.avatar_url as organizerAvatarUrl FROM event e LEFT JOIN category c ON e.category_id = c.id LEFT JOIN users u ON e.created_by = u.id WHERE e.status != 'finished'")
    List<Event> findActiveEvents();
} 