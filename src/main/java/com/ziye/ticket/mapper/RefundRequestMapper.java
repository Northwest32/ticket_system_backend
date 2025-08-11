package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.RefundRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RefundRequestMapper {
    
    @Insert("INSERT INTO refund_request (order_id, user_id, organizer_id, status, reason, created_at) " +
            "VALUES (#{orderId}, #{userId}, #{organizerId}, #{status}, #{reason}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RefundRequest refundRequest);
    
    @Select("SELECT * FROM refund_request WHERE id = #{id}")
    @Results({
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "organizerId", column = "organizer_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "processedAt", column = "processed_at")
    })
    RefundRequest findById(Long id);
    
    @Select("SELECT * FROM refund_request WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "organizerId", column = "organizer_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "processedAt", column = "processed_at")
    })
    List<RefundRequest> findByUserId(Long userId);
    
    @Select("SELECT * FROM refund_request WHERE organizer_id = #{organizerId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "organizerId", column = "organizer_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "processedAt", column = "processed_at")
    })
    List<RefundRequest> findByOrganizerId(Long organizerId);
    
    @Update("UPDATE refund_request SET status = #{status}, reply = #{reply}, processed_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("reply") String reply);
    
    @Select("SELECT * FROM refund_request WHERE order_id = #{orderId}")
    @Results({
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "organizerId", column = "organizer_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "processedAt", column = "processed_at")
    })
    RefundRequest findByOrderId(Long orderId);
} 