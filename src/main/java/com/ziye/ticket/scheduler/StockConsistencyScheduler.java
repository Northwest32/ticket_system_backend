package com.ziye.ticket.scheduler;

import com.ziye.ticket.entity.Event;
import com.ziye.ticket.mapper.EventMapper;
import com.ziye.ticket.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockConsistencyScheduler {
    private static final String STOCK_KEY_PREFIX = "event:stock:";
    //private static final String USER_KEY_PREFIX  = "event:buyer:";

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    // every 5 minutes
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncRedisAndDatabase() {
        List<Event> active = eventMapper.findActiveEvents();
        for (Event e : active) {
            String key = STOCK_KEY_PREFIX + e.getId();
            String val = redisTemplate.opsForValue().get(key);
            if (val == null) {
                // Redis lost, recover by database sales
                int sold = orderItemMapper.sumSoldByEvent(e.getId());
                int remain = Math.max(e.getCapacity() - sold, 0);
                redisTemplate.opsForValue().set(key, String.valueOf(remain));
                eventMapper.updateRemainingQuantity(e.getId(), remain);
            } else {
                int redisRemain = Integer.parseInt(val);
                // write back to database, if there is a difference
                if (e.getRemainingQuantity() == null || e.getRemainingQuantity() != redisRemain) {
                    eventMapper.updateRemainingQuantity(e.getId(), redisRemain);
                }
            }
        }
    }
} 