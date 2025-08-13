package com.ziye.ticket;

import com.ziye.ticket.entity.Event;
import com.ziye.ticket.mapper.EventMapper;
import com.ziye.ticket.service.SeckillService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.ziye.ticket.entity.User;
import com.ziye.ticket.mapper.UserMapper;

@SpringBootTest
public class SeckillConcurrencyTest {

    private static final Long EVENT_ID = 999L;
    private static final int INIT_STOCK = 10;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    public void testRedisConnection() {
        System.out.println("=== SeckillConcurrencyTest using default application.properties ===");
        System.out.println("Testing with local database and Redis configuration");
        try {
            String pong = redisTemplate.getConnectionFactory()
                                         .getConnection()
                                         .ping();
            System.out.println("Redis connected successfully: " + pong); 
        } catch (Exception e) {
            System.err.println("Redis connection failed: " + e.getMessage());
        }
    }

    @BeforeEach
    public void setUp() {
        // insert test users (id:6~35)
        for (long i = 6; i <= 35; i++) {
            if (userMapper.findById(i) == null) {
                User u = new User(i, "user" + i, "123456", "user" + i + "@test.com", "buyer", LocalDateTime.now(), null);
                userMapper.insertUser(u);
            }
        }
        System.out.println("---- inside setUp");
        // initialize database event information
        Event dbEvent = eventMapper.findById(EVENT_ID);
        if (dbEvent == null) {
            Event e = new Event();
            e.setId(EVENT_ID);
            e.setTitle("Test Event");
            e.setDescription("concurrency test");
            e.setImageUrl("");
            e.setLocation("online");
            e.setEventDate(LocalDateTime.now().plusDays(1));
            e.setPrice(100.0);
            e.setCapacity(INIT_STOCK);
            e.setStatus("upcoming");
            e.setCreatedBy(1L);
            e.setCreatedAt(LocalDateTime.now());
            e.setStartTime(LocalDateTime.now());
            e.setEndTime(LocalDateTime.now().plusDays(1));
            eventMapper.insertEvent(e);
        }

        // initialize Redis
        redisTemplate.opsForValue().set("event:stock:" + EVENT_ID, String.valueOf(INIT_STOCK));
        redisTemplate.delete("event:buyer:" + EVENT_ID);
        System.out.println("seckillService = " + seckillService);
        System.out.println("SetUp finished");
    }

    @Test
    public void testDatabaseConnection() {
        System.out.println("Testing database connection...");
        Event testEvent = eventMapper.findById(1L);
        System.out.println("Database connection successful, found event: " + (testEvent != null ? testEvent.getTitle() : "null"));
    }

    @Test
    public void concurrentSeckillTest() throws InterruptedException {
        System.out.println("---- inside seckill");
        int threadCount = 30; // include multiple attempts for the same user
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger success = new AtomicInteger(0);
        Set<Long> userSet = new HashSet<>();

        // 1. simulate user 1 to buy 5 tickets, each time 1 ticket
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                Long res = seckillService.seckill(1L, EVENT_ID, 1);
                long end = System.currentTimeMillis();
                System.out.println("User 1 result: " + res + ", time: " + (end - start) + " ms");
                if (res > 0) success.incrementAndGet();
                latch.countDown();
            });
        }

        // 2. other threads buy 1 ticket each
        for (int i = 0; i < threadCount - 5; i++) {
            long userId = i + 2; // start from 2 to avoid duplicate with user 1
            userSet.add(userId);
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                Long res = seckillService.seckill(userId, EVENT_ID, 1);
                long end = System.currentTimeMillis();
                System.out.println("User " + userId + " result: " + res + ", time: " + (end - start) + " ms");
                if (res > 0) success.incrementAndGet();
                latch.countDown();
            });
        }
        boolean finished = latch.await(10, TimeUnit.SECONDS);
        if (!finished) {
            System.err.println("⚠️ some threads not finished, possibly blocked or timed out!");
        }
        executor.shutdown();

                    // assert
        String remaining = redisTemplate.opsForValue().get("event:stock:" + EVENT_ID);
        Assertions.assertEquals("0", remaining);

        // User 1 should buy no more than 3 tickets (purchase limit)
        // Read count from Redis hash
        String user1Count = redisTemplate.<String, String>opsForHash()
                .get("event:buyer:" + EVENT_ID, String.valueOf(1L));
        int bought = user1Count == null ? 0 : Integer.parseInt(user1Count);
        Assertions.assertTrue(bought <= 3);

        //Ticket updatedTicket = ticketMapper.findById(TICKET_ID);
        //Assertions.assertEquals(0, updatedTicket.getRemainingQuantity().intValue());
    }
} 