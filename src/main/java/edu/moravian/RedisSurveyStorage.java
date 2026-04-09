package edu.moravian;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis specific implementation of the storage interface
 *
 * Stores persistent data in a Redis database
 */
public class RedisSurveyStorage implements SurveyStorage {

    private final JedisPool pool;
    private final MemorySurveyStorage memoryFallback = new MemorySurveyStorage();
    private final AtomicBoolean redisOnline = new AtomicBoolean(true);

    public RedisSurveyStorage(JedisPool pool) {
        this.pool = pool;
        checkInitialRedisState();
    }

    private void checkInitialRedisState() {
        try (Jedis j = pool.getResource()) {
            j.ping();
            redisOnline.set(true);
            System.out.println("✅ Redis connected successfully. Survey storage is using Redis.");
        } catch (Exception e) {
            redisOnline.set(false);
            System.out.println("⚠ Redis is offline at startup — using MEMORY fallback for survey storage.");
        }
    }

    private void markRedisOffline(Exception e) {
        if (redisOnline.compareAndSet(true, false)) {
            System.out.println("⚠ Redis went offline — switching survey storage to MEMORY fallback.");
            System.out.println("   Cause: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private void markRedisOnline() {
        if (redisOnline.compareAndSet(false, true)) {
            System.out.println("✅ Redis is back online — survey storage has reconnected to Redis.");
        }
    }

    private <T> T safeRedis(RedisAction<T> action, T fallback) {
        try (Jedis j = pool.getResource()) {
            T result = action.run(j);
            markRedisOnline();
            return result;
        } catch (Exception e) {
            markRedisOffline(e);
            return fallback;
        }
    }

    private void safeRedisVoid(RedisVoidAction action) {
        try (Jedis j = pool.getResource()) {
            action.run(j);
            markRedisOnline();
        } catch (Exception e) {
            markRedisOffline(e);
        }
    }

    private interface RedisAction<T> {
        T run(Jedis j);
    }

    private interface RedisVoidAction {
        void run(Jedis j);
    }

    @Override
    public boolean isSoloActive(String userId) {
        return safeRedis(
                j -> "1".equals(j.get("solo:active:" + userId)),
                memoryFallback.isSoloActive(userId)
        );
    }

    @Override
    public boolean isSoloPaused(String userId) {
        return safeRedis(
                j -> "1".equals(j.get("solo:paused:" + userId)),
                memoryFallback.isSoloPaused(userId)
        );
    }

    @Override
    public void setSoloActive(String userId, boolean active) {
        safeRedisVoid(j -> {
            if (active) {
                j.set("solo:active:" + userId, "1");
            } else {
                j.del("solo:active:" + userId);
            }
        });
        memoryFallback.setSoloActive(userId, active);
    }

    @Override
    public void setSoloPaused(String userId, boolean paused) {
        safeRedisVoid(j -> {
            if (paused) {
                j.set("solo:paused:" + userId, "1");
            } else {
                j.del("solo:paused:" + userId);
            }
        });
        memoryFallback.setSoloPaused(userId, paused);
    }

    @Override
    public int getSoloIndex(String userId) {
        return safeRedis(j -> {
            String v = j.get("solo:index:" + userId);
            return v == null ? 0 : Integer.parseInt(v);
        }, memoryFallback.getSoloIndex(userId));
    }

    @Override
    public void setSoloIndex(String userId, int index) {
        safeRedisVoid(j -> j.set("solo:index:" + userId, String.valueOf(index)));
        memoryFallback.setSoloIndex(userId, index);
    }

    @Override
    public void saveSoloAnswer(String userId, int index, String answer) {
        safeRedisVoid(j ->
                j.hset("solo:answers:" + userId, String.valueOf(index), answer)
        );
        memoryFallback.saveSoloAnswer(userId, index, answer);
    }

    @Override
    public Map<Integer, String> getAllSoloAnswers(String userId) {
        return safeRedis(j -> {
            Map<String, String> raw = j.hgetAll("solo:answers:" + userId);
            Map<Integer, String> out = new HashMap<>();
            for (Map.Entry<String, String> e : raw.entrySet()) {
                out.put(Integer.parseInt(e.getKey()), e.getValue());
            }
            return out;
        }, memoryFallback.getAllSoloAnswers(userId));
    }

    @Override
    public void resetSolo(String userId) {
        safeRedisVoid(j -> j.del(
                "solo:active:" + userId,
                "solo:paused:" + userId,
                "solo:index:" + userId
        ));
        memoryFallback.setSoloActive(userId, false);
        memoryFallback.setSoloPaused(userId, false);
        memoryFallback.setSoloIndex(userId, 0);
    }

    @Override
    public void hardDeleteSolo(String userId) {
        safeRedisVoid(j -> j.del(
                "solo:active:" + userId,
                "solo:paused:" + userId,
                "solo:index:" + userId,
                "solo:answers:" + userId
        ));
        memoryFallback.hardDeleteSolo(userId);
    }

    @Override
    public boolean isPairActive() {
        return safeRedis(
                j -> "1".equals(j.get("pair:active")),
                memoryFallback.isPairActive()
        );
    }

    @Override
    public boolean isPairPaused() {
        return safeRedis(
                j -> "1".equals(j.get("pair:paused")),
                memoryFallback.isPairPaused()
        );
    }

    @Override
    public void setPairActive(boolean active) {
        safeRedisVoid(j -> {
            if (active) {
                j.set("pair:active", "1");
            } else {
                j.del("pair:active");
            }
        });
        memoryFallback.setPairActive(active);
    }

    @Override
    public void setPairPaused(boolean paused) {
        safeRedisVoid(j -> {
            if (paused) {
                j.set("pair:paused", "1");
            } else {
                j.del("pair:paused");
            }
        });
        memoryFallback.setPairPaused(paused);
    }

    @Override
    public String getPairUser1() {
        return safeRedis(j -> j.get("pair:user1"), memoryFallback.getPairUser1());
    }

    @Override
    public String getPairUser2() {
        return safeRedis(j -> j.get("pair:user2"), memoryFallback.getPairUser2());
    }

    @Override
    public void setPairUser1(String userId) {
        safeRedisVoid(j -> j.set("pair:user1", userId));
        memoryFallback.setPairUser1(userId);
    }

    @Override
    public void setPairUser2(String userId) {
        safeRedisVoid(j -> j.set("pair:user2", userId));
        memoryFallback.setPairUser2(userId);
    }

    @Override
    public int getPairIndex() {
        return safeRedis(j -> {
            String v = j.get("pair:index");
            return v == null ? 0 : Integer.parseInt(v);
        }, memoryFallback.getPairIndex());
    }

    @Override
    public void setPairIndex(int index) {
        safeRedisVoid(j -> j.set("pair:index", String.valueOf(index)));
        memoryFallback.setPairIndex(index);
    }

    @Override
    public int getPairTurn() {
        return safeRedis(j -> {
            String v = j.get("pair:turn");
            return v == null ? 1 : Integer.parseInt(v);
        }, memoryFallback.getPairTurn());
    }

    @Override
    public void setPairTurn(int turn) {
        safeRedisVoid(j -> j.set("pair:turn", String.valueOf(turn)));
        memoryFallback.setPairTurn(turn);
    }

    @Override
    public void savePairAnswerUser1(int index, String answer) {
        safeRedisVoid(j ->
                j.hset("pair:answers:1", String.valueOf(index), answer)
        );
        memoryFallback.savePairAnswerUser1(index, answer);
    }

    @Override
    public void savePairAnswerUser2(int index, String answer) {
        safeRedisVoid(j ->
                j.hset("pair:answers:2", String.valueOf(index), answer)
        );
        memoryFallback.savePairAnswerUser2(index, answer);
    }

    @Override
    public Map<Integer, String> getAllPairAnswersUser1() {
        return safeRedis(j -> {
            Map<String, String> raw = j.hgetAll("pair:answers:1");
            Map<Integer, String> out = new HashMap<>();
            for (Map.Entry<String, String> e : raw.entrySet()) {
                out.put(Integer.parseInt(e.getKey()), e.getValue());
            }
            return out;
        }, memoryFallback.getAllPairAnswersUser1());
    }

    @Override
    public Map<Integer, String> getAllPairAnswersUser2() {
        return safeRedis(j -> {
            Map<String, String> raw = j.hgetAll("pair:answers:2");
            Map<Integer, String> out = new HashMap<>();
            for (Map.Entry<String, String> e : raw.entrySet()) {
                out.put(Integer.parseInt(e.getKey()), e.getValue());
            }
            return out;
        }, memoryFallback.getAllPairAnswersUser2());
    }

    @Override
    public void resetPair() {
        safeRedisVoid(j -> j.del(
                "pair:active",
                "pair:paused",
                "pair:index",
                "pair:turn",
                "pair:user1",
                "pair:user2"
        ));
        memoryFallback.setPairActive(false);
        memoryFallback.setPairPaused(false);
        memoryFallback.setPairIndex(0);
        memoryFallback.setPairTurn(1);
        memoryFallback.setPairUser1(null);
        memoryFallback.setPairUser2(null);
    }

    @Override
    public void hardDeletePair() {
        safeRedisVoid(j -> j.del(
                "pair:active",
                "pair:paused",
                "pair:index",
                "pair:turn",
                "pair:user1",
                "pair:user2",
                "pair:answers:1",
                "pair:answers:2"
        ));
        memoryFallback.hardDeletePair();
    }
}