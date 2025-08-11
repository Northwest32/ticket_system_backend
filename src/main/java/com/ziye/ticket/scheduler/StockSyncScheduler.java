package com.ziye.ticket.scheduler;

import com.ziye.ticket.entity.Event;
import com.ziye.ticket.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StockSyncScheduler {
    private static final String STOCK_KEY_PREFIX = "event:stock:";
    private static final String USER_KEY_PREFIX = "event:buyer:";

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 * * * * ?")
    public void syncStockForEndedEvents() {
        List<Event> ended = eventMapper.findEventsEnded(LocalDateTime.now());
        for (Event event : ended) {
            String stockKey = STOCK_KEY_PREFIX + event.getId();
            redisTemplate.delete(stockKey);
            redisTemplate.delete(USER_KEY_PREFIX + event.getId());
            eventMapper.markFinished(event.getId());
        }
    }
} 