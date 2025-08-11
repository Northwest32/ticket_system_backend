package com.ziye.ticket.service;

import com.ziye.ticket.entity.Order;
import com.ziye.ticket.entity.OrderItem;
import com.ziye.ticket.entity.Event;
import com.ziye.ticket.mapper.OrderItemMapper;
import com.ziye.ticket.mapper.OrderMapper;
import com.ziye.ticket.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class SeckillService {
    private static final String STOCK_KEY_PREFIX = "event:stock:";
    private static final String USER_KEY_PREFIX = "event:buyer:";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private EventMapper eventMapper;

    private RedisScript<Long> stockScript;

    @PostConstruct
    public void loadScript() {
        stockScript = RedisScript.of(
        new ClassPathResource("seckill.lua"),
        Long.class
    );
}

    @Transactional
    public Long seckill(Long userId, Long eventId, int quantity) {
        System.out.println("===> start, user=" + userId);
        
        String stockKey = STOCK_KEY_PREFIX + eventId;
        String userKey = USER_KEY_PREFIX + eventId;

        Long result = redisTemplate.execute(stockScript, Arrays.asList(stockKey, userKey), String.valueOf(userId), String.valueOf(quantity));
        System.out.println("Lua result = " + result);

        if (result == null) return -3L;
        if (result == -1) return -1L;
        if (result == -2) return -2L; // over purchase limit

        // 2. get event info
        Event event = eventMapper.findById(eventId);
        if (event == null) {
            return -4L; // event not found
        }

        // 3. create order
        Order order = new Order();
        order.setUserId(userId);
        order.setEventId(eventId);
        order.setQuantity(quantity);
        order.setStatus("PAID"); // seckill success means paid
        order.setCreateTime(LocalDateTime.now());
        order.setPayTime(LocalDateTime.now()); // set pay time
        try {
            orderMapper.insertOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
            return -4L; // DB error
        }

        // 4. order item price
        double unitPrice = event.getPrice();

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setEventId(eventId);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        try {
            orderItemMapper.insertOrderItem(item);
        } catch (Exception e) {
            e.printStackTrace();
            return -4L; // DB error
        }

        // if need to write back to database stock, can be done in the event end scheduled task

        return order.getId(); // return order ID
    }
} 