package com.xkcoding.use.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 简单K-V操作
 *
 * @author zhangxinyu
 * @date 2023/5/10
 **/
@Slf4j
public class RedisTemplateTest extends SpringBootDemoUseRedisApplicationTests {
    @Autowired
    @Qualifier("redisTemplateJackson")
    private RedisTemplate<String, Object> redisTemplateJson;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private final String key = "cache:public:key";

    private BoundValueOperations<String,String> publicValueOps;

    @PostConstruct
    public void init() {
        log.debug("======================init==========================");
        // 初始设置一个key
        stringRedisTemplate.opsForValue().set(key, "public_value");

        // boundValueOps可以对key进行绑定,使用opsForValue每次需要显示指定key
        publicValueOps = stringRedisTemplate.boundValueOps(key);
    }

    /**
     * 从指定offset位置替换，替换的字符个数为指定的字串的长度
     */
    @Test
    public void testSetOffset() {
        stringRedisTemplate.opsForValue().set(key, "hello", 2);
        log.debug("key:{}, old:{}, new:{}", key, "public_value", stringRedisTemplate.opsForValue().get(key));
        testDeleteKey();
    }

    /**
     * 设置变量值，并指定过期时间
     */
    @Test
    public void testSetExpire() {
        stringRedisTemplate.opsForValue().set(key, "hello", 2, TimeUnit.SECONDS);
        log.debug("key:{}, value:{}", key, stringRedisTemplate.opsForValue().get(key));
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("key:{}, value:{}", key, stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * 批量设置key和批量获取key的value
     */
    @Test
    public void testMultiSetAndGet() {
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("valueMap1", "map1");
        valueMap.put("valueMap2", "map2");
        valueMap.put("valueMap3", "map3");
        stringRedisTemplate.opsForValue().multiSet(valueMap);

        List<String> paraList = new ArrayList<>();
        paraList.add("valueMap1");
        paraList.add("valueMap2");
        paraList.add("valueMap3");
        List<String> valueList = stringRedisTemplate.opsForValue().multiGet(paraList);
        log.debug("multiValues:{}", valueList);

        testDeleteKey();
    }

    /**
     * key不存在则新增，key存在则不做操作
     */
    @Test
    public void testSetIfAbsent() {
        stringRedisTemplate.opsForValue().setIfAbsent(key, "new_public_value");
        log.debug("key:{}, value:{}", key, stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * key存在不做操作，不存在则新增
     */
    @Test
    public void testMultiIfAbsent() {
        HashMap<String, String> valueMap = new HashMap<>();
        valueMap.put("valueMap1", "map1");
        valueMap.put("valueMap2", "map2");
        valueMap.put("valueMap3", "map3");

        stringRedisTemplate.opsForValue().multiSetIfAbsent(valueMap);
    }

    /**
     * 在原有的值基础上新增字符串到末尾。
     */
    @Test
    public void testAppend() {
        stringRedisTemplate.opsForValue().append(key, "追加数据");
        String value = stringRedisTemplate.opsForValue().get(key);
        log.debug("key -> {}, value - > {}", key, value);
    }

    @Test
    public void testSize() {
        log.debug("key:{}, value.size:{}", key, stringRedisTemplate.opsForValue().size(key));
    }

    @Test
    public void testGet() {
        String key = "cache:key:set:get";
        stringRedisTemplate.opsForValue().set(key, "luncy");
        String value = stringRedisTemplate.opsForValue().get(key);
        log.debug("[key:{},value:{}]", key, value);
    }

    @Test
    public void testGetSubstr() {
        String value = stringRedisTemplate.opsForValue().get(key, 0, 5);
        log.debug("[key:{},value:{}]", key, value);
    }

    /**
     * 获取key对应的原值，并设置新的value
     */
    @Test
    public void testGetAndSet() {
        String value = stringRedisTemplate.opsForValue().getAndSet(key, "new_value");
        log.debug("key -> {}, value - > {}", key, value);
        log.debug("key -> {}, value - > {}", key, stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * bitmap：相当于一个byte数组，只能存0，1，数组的下标是偏移量
     * <p>
     * <p>
     * redis命令：
     * SETBIT key offset value
     * GETBIT key offset
     */
    @Test
    public void testSetBit() {
        String key = "cache:set:bit";
        stringRedisTemplate.opsForValue().setBit(key, 1, true);
        stringRedisTemplate.opsForValue().setBit(key, 0, true);
        stringRedisTemplate.opsForValue().setBit(key, 2, true);
        Boolean bit = stringRedisTemplate.opsForValue().getBit(key, 1);
        log.debug("key：{},bitValue:{}", key, bit);
    }

    /**
     * bitmap
     * 统计bitmap中位上位1的个数
     * rdis命令：bitcount key [offset_start, offset_end]
     */
    @Test
    public void testBitcount() {
        String key = "cache:set:bit";
        Long count = stringRedisTemplate.execute((RedisCallback<Long>)con -> con.bitCount(key.getBytes()));
        log.debug("bitmap 1 count:{}", count);
    }

    /**
     * 以增量的方式将Long值存储在变量中
     */
    @Test
    public void testIncrement() {
        String key = "cache:redis:increment";
        Long increment = stringRedisTemplate.opsForValue().increment(key);
        log.debug("key:{}, value:{}", key, increment);
    }

    // ====================================redisTemplate直接使用的方法==========================================

    @Test
    public void testDeleteKey() {
        redisTemplate.delete(key);
        log.debug("key:{}, value:{}", key, redisTemplate.opsForValue().get(key));
    }

    @Test
    public void testDeleteKeys() {
        stringRedisTemplate.delete(Arrays.asList(key, key, key));
    }

    @Test
    public void testExpire() {
        // 获取过期时间
        redisTemplate.getExpire(key);
        // 设置过期时间
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);
    }

    @Test
    public void testHasKey() {
        log.debug("key:{}, exist:{}", key, stringRedisTemplate.hasKey(key));
    }

    // ====================================测试boundValueOps==========================================

    @Test
    public void testBoundValueOps() {
        log.debug("key:{},value:{}", key, publicValueOps.get());
        publicValueOps.expire(2, TimeUnit.SECONDS);
        // TODO
    }

}
