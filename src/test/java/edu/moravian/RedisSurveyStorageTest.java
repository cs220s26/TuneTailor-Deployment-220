package edu.moravian;

import com.github.fppt.jedismock.RedisServer;
import org.junit.jupiter.api.*;
import redis.clients.jedis.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RedisSurveyStorageTest {

    private static RedisServer server;
    private JedisPool pool;

    @BeforeAll
    static void setupServer() throws IOException {
        server = RedisServer.newRedisServer();
        server.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        server.stop();
    }

    @BeforeEach
    void setupEach() {
        pool = new JedisPool(server.getHost(), server.getBindPort());

        try (Jedis j = pool.getResource()) {
            j.flushAll();
        }
    }

    @Test
    void pairInactiveByDefaultAfterReset() {
        RedisSurveyStorage s = new RedisSurveyStorage(pool);
        s.resetPair();
        assertFalse(s.isPairActive());
    }

    @Test
    void soloStoredInRedis() {
        RedisSurveyStorage s = new RedisSurveyStorage(pool);
        s.setSoloActive("u", true);
        assertTrue(s.isSoloActive("u"));
    }

    @Test
    void pairResetClearsRedis() {
        RedisSurveyStorage s = new RedisSurveyStorage(pool);
        s.setPairActive(true);
        s.setPairUser1("x");
        s.resetPair();

        assertFalse(s.isPairActive());
        assertNull(s.getPairUser1());
    }
}
