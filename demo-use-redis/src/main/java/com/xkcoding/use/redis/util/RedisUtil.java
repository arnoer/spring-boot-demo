package com.xkcoding.use.redis.util;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @author zhangxinyu
 * @date 2023/5/8
 **/
@AllArgsConstructor
public class RedisUtil {

    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储时，value为一个List集合对象
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return (List<T>)value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
