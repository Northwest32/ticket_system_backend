package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.Order;
import com.ziye.ticket.entity.RefundRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO orders (user_id, event_id, quantity, status, create_time, pay_time) VALUES (#{userId}, #{eventId}, #{quantity}, #{status}, #{createTime}, #{payTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Select("SELECT o.id, o.user_id, o.event_id, o.quantity, o.status, o.create_time, o.pay_time, " +
            "e.title as eventTitle, e.location as venue, e.price * o.quantity as totalAmount " +
            "FROM orders o " +
            "LEFT JOIN event e ON o.event_id = e.id " +
            "WHERE o.user_id = #{userId} " +
            "ORDER BY o.create_time DESC")
    List<Order> findByUserId(Long userId);

    @Select("SELECT o.id, o.user_id, o.event_id, o.quantity, o.status, o.create_time, o.pay_time, " +
            "e.title as eventTitle, e.location as venue, e.price * o.quantity as totalAmount " +
            "FROM orders o " +
            "LEFT JOIN event e ON o.event_id = e.id " +
            "WHERE o.id = #{id}")
    Order findById(Long id);

    @Update("UPDATE orders SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT o.id, o.user_id, o.event_id, o.quantity, o.status, o.create_time, o.pay_time, " +
            "e.title as eventTitle, e.location as venue, e.price * o.quantity as totalAmount " +
            "FROM orders o " +
            "LEFT JOIN event e ON o.event_id = e.id " +
            "WHERE o.event_id = #{eventId} " +
            "ORDER BY o.create_time DESC")
    List<Order> findByEventId(Long eventId);
    
    @Select("SELECT * FROM refund_request WHERE order_id = #{orderId}")
    RefundRequest findRefundRequestByOrderId(Long orderId);
} 