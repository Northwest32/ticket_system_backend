package com.ziye.ticket.mapper;

import com.ziye.ticket.entity.OrderItem;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderItemMapper {
    @Insert("INSERT INTO order_item (order_id, event_id, quantity, unit_price) VALUES (#{orderId}, #{eventId}, #{quantity}, #{unitPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrderItem(OrderItem item);

    @Select("SELECT IFNULL(SUM(quantity),0) FROM order_item WHERE event_id = #{eventId}")
    Integer sumSoldByEvent(Long eventId);
} 