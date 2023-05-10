package com.xkcoding.use.redis;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 针对map类型的数据操作
 *
 * Redis hash是一个键值对集合，一个键对应多个值对[field,value]，key-field+value形式，可以理解成java的map< string,map<string,string>>
 * ，三个string依次对应key、field、value。
 *
 * @author zhangxinyu
 * @date 2023/5/10
 **/
@Slf4j
public class RedisTemplateHashTest extends SpringBootDemoUseRedisApplicationTests {

    @Autowired
    @Qualifier("redisTemplateJackson")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    private String key = "redis:hash:public:key";

    private BoundHashOperations<String, String, String> boundHashOps;

    private HashOperations<String, String, String> hashOps;


    @PostConstruct
    public void init() {
        log.debug("======================init==========================");
        redisTemplate.opsForHash().put(key, "smallkey", "smallValue");
        // 绑定key，无需显示指定key
        boundHashOps = stringRedisTemplate.boundHashOps(key);

        hashOps = stringRedisTemplate.opsForHash();
    }

    @Test
    public void testPutAll() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("diel", "lucys");
        hashMap.put("island", "beids");
        hashOps.putAll(key, hashMap);

        log.debug("key:{}, entries:{}", key, JSONUtil.toJsonStr(boundHashOps.entries()));
    }


    @Test
    public void testExtractKeys() {
        Set<String> keys = hashOps.keys(key);
        log.debug("key:{}, keys:{}", key, JSONUtil.toJsonStr(keys));
    }

    /**
     * 获取所有values
     */
    @Test
    public void testExtractValues() {
        // boundHashOps.values();
        List<String> values = hashOps.values(key);
        log.debug("key:{}, values:{}", key, values);

    }

    /**
     * 获取单个key的value
     */
    @Test
    public void testExtractValueOrValuesByKey() {
        String value = hashOps.get(key, "smallkey");
        log.debug("key:{}, key:{}, value:{}", key, "smallkey", value);
    }

    /**
     * 获取所有键值对集合
     */
    @Test
    public void testExtractEntries() {
        // boundHashOps.entries();
        Map<String, String> entries = hashOps.entries(key);
        log.debug("key:{}, values:{}", key, JSONUtil.toJsonStr(entries));
    }

    /**
     * 删除小key
     */
    @Test
    public void testDelete() {
        hashOps.delete(key, "smallkey");
        log.debug("key:{}, small key:{}, value:{}", key, "smallkey", hashOps.get(key, "smallkey"));
        // 删除大key
        redisTemplate.delete(key);
    }

    /**
     * 设置大key过期时间
     */
    @Test
    public void testExpire() {
        boundHashOps.expire(1, TimeUnit.SECONDS);

        redisTemplate.expire(key, 1, TimeUnit.SECONDS);
    }

    @Test
    public void testHasKey() {
        Boolean result = hashOps.hasKey(key, "smallkey");
        log.debug("key:{}, has:{}, result:{}", key, "smallkey", result);

        boundHashOps.hasKey("smallkey");
    }
}
