package com.xkcoding.use.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangxinyu
 * @date 2023/5/10
 **/
@Slf4j
public class RedisTemplateListTest extends SpringBootDemoUseRedisApplicationTests {

    @Autowired
    @Qualifier("redisTemplateJackson")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    private final String key = "redis:list:public:key";

    private BoundListOperations<String, String> boundListOps;
    private ListOperations<String, String> listOps;

    @PostConstruct
    public void init() {
        log.debug("======================init==========================");

        boundListOps = stringRedisTemplate.boundListOps(key);
        listOps = stringRedisTemplate.opsForList();

        listOps.leftPushAll(key, Arrays.asList("listcy", "duicys"));
    }

    /**
     * 存取
     */
    @Test
    public void testPushOrPushAll() {
        listOps.leftPush(key, "luncy");
        listOps.rightPush(key, "goios");
        boundListOps.leftPush("pcsiog");

        log.debug("key:{}, values:{}", key, boundListOps.range(0, boundListOps.size()));
    }

    /**
     * 根据索引获取value
     */
    @Test
    public void testIndex() {
        String value = boundListOps.index(1);
        log.debug("key:{}, index:{}, value:{}", key, 1, value);
    }

    /**
     * 弹出元素
     */
    @Test
    public void testPop() {
        log.debug("key:{}, values:{}", key, boundListOps.range(0, boundListOps.size()));
        String leftValue = listOps.leftPop(key);
        String rightValue = listOps.rightPop(key);
        log.debug("左侧弹出一个元素：{}", leftValue);
        log.debug("右侧弹出一个元素：{}", rightValue);
    }

    /**
     * 获取List的所有数据
     */
    @Test
    public void testExtractAll() {
        List<String> values = boundListOps.range(0, boundListOps.size());
        log.debug("key:{}, values:{}", key, values);
    }

    /**
     * 根据索引修改
     */
    @Test
    public void testSet() {
        log.debug("key:{}, index:{}, value:{}", key, 1, listOps.index(key, 1));
        listOps.set(key, 1, "setNewValue");
        log.debug("key:{}, index:{}, value:{}", key, 1, listOps.index(key, 1));
    }

    /**
     * 移除N个指定元素
     */
    @Test
    public void testRemove() {
        log.debug("key:{}, values:{}", key, boundListOps.range(0, boundListOps.size()));

        Long count = boundListOps.remove(3L, "listcy");
        log.debug("key:{}, values:{}, count:{}", key, boundListOps.range(0, boundListOps.size()), count);
    }

    /**
     * 设置key有效期
     */
    @Test
    public void testExpire() {
        boundListOps.expire(2, TimeUnit.SECONDS);
    }
}
