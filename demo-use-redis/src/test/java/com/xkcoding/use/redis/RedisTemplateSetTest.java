package com.xkcoding.use.redis;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangxinyu
 * @date 2023/5/10
 **/
@Slf4j
public class RedisTemplateSetTest extends SpringBootDemoUseRedisApplicationTests {

    @Autowired
    @Qualifier("redisTemplateJackson")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    private final String key = "redis:set:public:key";

    private BoundSetOperations<String, String> boundSetOps;

    private SetOperations<String, String> setOps;

    @PostConstruct
    public void init() {
        log.debug("======================init==========================");
        stringRedisTemplate.opsForSet().add(key,  "setvalue");

        // 绑定key，无需显示指定key
        boundSetOps = stringRedisTemplate.boundSetOps(key);

        setOps = stringRedisTemplate.opsForSet();
    }

    @Test
    public void testAdd() {
        boundSetOps.add("kdao", "bill");
        setOps.add(key, "foslw", "qus");
        log.debug("key:{}, values:{}", key, JSONUtil.toJsonStr(boundSetOps.members()));
    }

    /**
     * 获取所有的元素
     */
    @Test
    public void testMembers() {
        Set<String> members = setOps.members(key);
        log.debug("key:{}, values:{}", key, members);
    }

    /**
     * 验证value在Set中是否存在
     */
    @Test
    public void testValueExist() {
        Boolean exist = setOps.isMember(key, "setvalue");
        log.debug("key:{}, {} {}", key, "setvalue", exist);
    }

    /**
     * 移除指定的元素
     */
    @Test
    public void testRemoveValue() {
        setOps.remove(key, "setvalue");
        log.debug("key:{}, value:{}", key, JSONUtil.toJsonStr(setOps.members(key)));
    }

    @Test
    public void testExpire() {
        stringRedisTemplate.expire(key, 1, TimeUnit.SECONDS);
        boundSetOps.expire(1, TimeUnit.SECONDS);
    }

    @Test
    public void testDelete() {
        stringRedisTemplate.delete(key);
    }
}
