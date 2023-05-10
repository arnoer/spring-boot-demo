package com.xkcoding.use.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <a href="https://www.cnblogs.com/wuyizuokan/p/11108417.html">redis数据结构ZSet</a>
 *
 * @author zhangxinyu
 * @date 2023/5/10
 **/
@Slf4j
public class RedisTemplateZsetTest extends SpringBootDemoUseRedisApplicationTests {
    @Autowired
    @Qualifier("redisTemplateJackson")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    private final String key = "redis:zset:public:key";
    private BoundZSetOperations<String, String> boundZsetOps;
    private ZSetOperations<String, String> zsetOps;

    @PostConstruct
    public void init() {
        log.debug("======================init==========================");

        boundZsetOps = stringRedisTemplate.boundZSetOps(key);
        zsetOps = stringRedisTemplate.opsForZSet();

        DefaultTypedTuple<String> p1 = new DefaultTypedTuple<>("wdisos", 0.1D);
        DefaultTypedTuple<String> p2 = new DefaultTypedTuple<>("suisi", 0.3D);
        boundZsetOps.add(new HashSet<>(Arrays.asList(p1, p2)));

        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));

    }

    /**
     * 添加值
     */
    @Test
    public void testAdd() {
        boundZsetOps.add("lucnsy", 20D);
        zsetOps.add(key, "diaogo", 12D);

        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));

        testDelete();
    }

    /**
     * 集合中插入多个元素
     */
    @Test
    public void testAddMulti() {
        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));
        DefaultTypedTuple<String> p1 = new DefaultTypedTuple<>("clis", 2.1D);
        DefaultTypedTuple<String> p2 = new DefaultTypedTuple<>("kido", 3.3D);
        boundZsetOps.add(new HashSet<>(Arrays.asList(p1, p2)));
        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));

        testDelete();
    }

    /**
     * 为指定元素加分（Double类型）
     */
    @Test
    public void testIncrementScore() {
        testScore();
        boundZsetOps.incrementScore("wdisos", 12D);
        testScore();
    }

    /**
     * 按照排名先后(从小到大)打印指定区间内的元素, -1为打印全部
     */
    @Test
    public void testRange() {
        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));
    }

    /**
     * 返回集合内元素的排名，以及分数（从小到大）
     */
    @Test
    public void testRangeWithScore() {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = boundZsetOps.rangeWithScores(0L, 90L);
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            log.debug("value:{}, score:{}", typedTuple.getValue(), typedTuple.getScore());
        }
    }

    /**
     * 返回指定成员的排名
     */
    @Test
    public void testRank() {
        Long rank = boundZsetOps.rank("wdisos");
        // 从小到大
        log.debug("key:{}, value:{}, rank:{}", key, "wdisos", rank);
        // 从大到小
        Long reverseRank = boundZsetOps.reverseRank("wdisos");
        log.debug("key:{}, value:{}, rank:{}", key, "wdisos", reverseRank);
    }

    /**
     * 获得指定元素的分数
     */
    @Test
    public void testScore() {
        log.debug("key:{}, value:{}, score:{}", key, "wdisos", boundZsetOps.score("wdisos"));
    }

    /**
     * 返回集合内指定分数范围的成员个数
     */
    @Test
    public void testCount() {
        log.debug("key:{}, lower:{}, higher:{}, count:{}", key, 0.2D, 12D, boundZsetOps.count(0.2D, 12D));
    }

    /**
     * 返回集合内元素在指定分数范围内（从小到大）的排序结果
     */
    @Test
    public void testRankByScore() {
        log.debug("key:{}, lower:{}, higher:{}, rankList:{}", key, 0.2D, 12D, boundZsetOps.rangeByScore(0.2D, 12D));
    }

    /**
     * 从集合中删除指定元素
     */
    @Test
    public void testRemove() {
        testRange();
        boundZsetOps.remove("suisi");
        testRange();
    }

    /**
     * 删除指定索引范围的元素（Long类型）
     */
    @Test
    public void testRemoveRange() {
        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));
        boundZsetOps.removeRange(0L, 3L);
        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));
    }

    /**
     * 删除指定分数范围内的元素（Double类型）
     */
    @Test
    public void testRemoveRangeByScores() {
        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));
        boundZsetOps.removeRangeByScore(0D, 3D);
        log.debug("key:{}, values:{}", key, boundZsetOps.range(0, -1));
    }

    @Test
    public void testDelete() {
        redisTemplate.delete(key);
    }

}
