package com.xkcoding.use.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xkcoding.use.redis.entity.User;
import com.xkcoding.use.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 数据的序列化策略：序列化/反序列化
 *
 * JdkSerializationRedisSerializer：POJO对象的存取场景，使用JDK本身序列化机制，将pojo类通过ObjectInputStream/ObjectOutputStream
 * 进行序列化操作，最终redis-server中将存储字节序列。是目前最常用的序列化策略。
 * <p>
 * StringRedisSerializer：Key或者value为字符串的场景，根据指定的charset对数据的字节序列编码成string，是“new String(bytes, charset)”和“string
 * .getBytes(charset)”的直接封装。是最轻量级和高效的策略。
 * <p>
 * JacksonJsonRedisSerializer：jackson-json工具提供了javabean与json之间的转换能力，可以将pojo实例序列化成json格式存储在redis中，也可以将json格式的数据转换成pojo
 * 实例。因为jackson工具在序列化和反序列化时，需要明确指定Class类型，因此此策略封装起来稍微复杂。【需要jackson-mapper-asl工具支持】
 *
 *
 * <p>
 * 如果使用redisTemplate获取数据为空，查看redisTemplate的序列化策略，比如redis ui可读，使用jdk序列化策略的redisTemplate就可能无法获取到数据
 *
 * @author zhangxinyu
 * @date 2023/5/6
 **/
@Slf4j
public class RedisSerializerTest extends SpringBootDemoUseRedisApplicationTests{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplateJackson")
    private RedisTemplate<String, Object> redisTemplateJackson;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 使用Jackson2JsonRedisSerializer json序列化，ObjectMapper未设置enableDefaultTyping()的处理方式
     */
    @Test
    public void redisTemplateJacksonTest() {
        ValueOperations<String, Object> jacksonOps = redisTemplateJackson.opsForValue();
        // 存储单个对象
        jacksonOps.set("cache:user:1001", new User().setId(1001L).setName("nancy"));
        Object o = jacksonOps.get("cache:user:1001");
        // 未指定输入类型，Java解析的是LinkedHashMap数据，进行处理
        ObjectMapper om = new ObjectMapper();
        User user = om.convertValue(o, new TypeReference<User>() {
        });
        log.debug("[user] -> {}", user);
        // 存储数组对象
        jacksonOps.set("cache:user:all", Arrays.asList(new User().setId(1002L).setName("kotlin"),
            new User().setId(1003L).setName("lucy")));
        Object oAll = jacksonOps.get("cache:user:all");

        List<User> userList = om.convertValue(oAll, new TypeReference<List<User>>() {
        });

        log.debug("[userList] -> {}", userList);
    }

    /**
     * 使用Generic~或者Jackson2~设置了序列化类型，可以直接转换
     */
    public void redisTemplateJsonTest() {
        ValueOperations<String, Object> jacksonOps = redisTemplateJackson.opsForValue();
        jacksonOps.set("cache:user:json:1001", new User().setId(1004L).setName("kios"));

        User user = (User)jacksonOps.get("cache:user:json:1001");
        System.out.println(user);

        // 指定了输入类型，可以直接转换
        List<User> users = (List<User>)jacksonOps.get("cache:user:json:all");
        List<User> list = redisUtil.getList("cache:user:json:all", User.class);

        System.out.println(users);
    }

    @Test
    public void test() {
        String key_1 = "redis::origin";
        String key_2 = "redis::string";

        redisTemplate.opsForValue().set(key_1, "祖国河山");
        stringRedisTemplate.opsForValue().set(key_2, "beautiful rivers and mountains of a country");

        Object o = redisTemplate.opsForValue().get(key_1);
        String o1 = stringRedisTemplate.opsForValue().get(key_2);

        System.out.println("===================");
    }

}
