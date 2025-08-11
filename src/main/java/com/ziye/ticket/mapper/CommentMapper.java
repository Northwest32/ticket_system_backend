package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.Comment;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("INSERT INTO comment(content, from_user_id, to_event_id, to_organizer_id, rating, created_at, parent_comment_id) VALUES(#{content},#{fromUserId},#{toEventId},#{toOrganizerId},#{rating},NOW(),#{parentCommentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment c);

    @Select("""
        SELECT c.*, u.username as fromUserName, u.user_type as fromUserType,
               CASE WHEN o.id IS NOT NULL THEN true ELSE false END as hasPurchased
        FROM comment c 
        LEFT JOIN users u ON c.from_user_id = u.id 
        LEFT JOIN orders o ON c.from_user_id = o.user_id AND c.to_event_id = o.event_id AND o.status = 'PAID'
        WHERE c.to_event_id=#{eventId} AND c.parent_comment_id IS NULL 
        ORDER BY c.created_at DESC
        """)
    List<Comment> findTopByEvent(Long eventId);

    @Select("""
        SELECT c.*, u.username as fromUserName, u.user_type as fromUserType,
               CASE WHEN o.id IS NOT NULL THEN true ELSE false END as hasPurchased
        FROM comment c 
        LEFT JOIN users u ON c.from_user_id = u.id 
        LEFT JOIN orders o ON c.from_user_id = o.user_id AND c.to_event_id = o.event_id AND o.status = 'PAID'
        WHERE c.to_organizer_id=#{orgId} AND c.parent_comment_id IS NULL 
        ORDER BY c.created_at DESC
        """)
    List<Comment> findTopByOrganizer(Long orgId);

    @Select("SELECT * FROM comment WHERE parent_comment_id=#{cid} ORDER BY created_at ASC")
    List<Comment> findReplies(Long cid);

    @Select("""
        SELECT c.*, u.username as fromUserName, u.user_type as fromUserType,
               CASE WHEN o.id IS NOT NULL THEN true ELSE false END as hasPurchased
        FROM comment c
        LEFT JOIN users u ON c.from_user_id = u.id
        LEFT JOIN event e ON c.to_event_id = e.id
        LEFT JOIN comment pc ON c.parent_comment_id = pc.id
        LEFT JOIN orders o ON c.from_user_id = o.user_id AND c.to_event_id = o.event_id AND o.status = 'PAID'
        WHERE
            c.to_organizer_id = #{uid}
            OR (c.to_event_id IS NOT NULL AND e.created_by = #{uid})
            OR (pc.from_user_id = #{uid})
        ORDER BY c.created_at DESC
        """)
        
    List<Comment> findReceived(Long uid);

    @Select("""
        SELECT c.*, u.username as fromUserName, u.user_type as fromUserType,
               CASE WHEN o.id IS NOT NULL THEN true ELSE false END as hasPurchased
        FROM comment c 
        LEFT JOIN users u ON c.from_user_id = u.id 
        LEFT JOIN orders o ON c.from_user_id = o.user_id AND c.to_event_id = o.event_id AND o.status = 'PAID'
        WHERE c.from_user_id=#{uid}
        """)
    List<Comment> findGiven(Long uid);

    @Select("SELECT * FROM comment WHERE id=#{id}")
    Comment findById(Long id);

    @Delete("DELETE FROM comment WHERE id=#{id} AND from_user_id=#{userId}")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("DELETE FROM comment WHERE parent_comment_id=#{parentId}")
    int deleteRepliesByParentId(@Param("parentId") Long parentId);
} 