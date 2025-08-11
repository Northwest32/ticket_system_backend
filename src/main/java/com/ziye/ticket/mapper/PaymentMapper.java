package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.Payment;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentMapper {
    @Insert("INSERT INTO payment(order_id, amount, payment_method, status, created_at, updated_at) " +
            "VALUES(#{orderId}, #{amount}, #{paymentMethod}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Payment payment);

    @Update("UPDATE payment SET status=#{status}, updated_at=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT * FROM payment WHERE order_id = #{orderId}")
    Payment findByOrderId(Long orderId);
} 