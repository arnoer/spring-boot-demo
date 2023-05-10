package com.xkcoding.use.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xkcoding.use.redis.util.RedisUtil;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * redis配置
 * </p>
 *
 * @author zhangxinyu
 * @date Created in 2018-11-15 16:41
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching
public class RedisConfig {

    // ==========默认情况下的模板只能支持RedisTemplate<String, String>===================

    /**
     * TODO: 如果需要将Spring未配置的转换器中的Java对象转为String,需要配置自定义的Converter<S, T>,将Object转为String,否则setKey时会提示noConverter
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "redisTemplateString")
    public RedisTemplate<String, Object> redisTemplateString(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(String.class));
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class, StandardCharsets.UTF_8));
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean(name = "redisTemplateJackson")
    public RedisTemplate<String, Object> redisTemplateJackson(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 关闭启用默认配置
        redisTemplate.setEnableDefaultSerializer(false);

        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonSerializer());

        redisTemplate.setHashKeySerializer(stringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonSerializer());

        // 设置支持事物
        redisTemplate.setEnableTransactionSupport(true);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "redisUtil")
    @ConditionalOnBean(RedisTemplate.class)
    public RedisUtil redisUtils(RedisTemplate<String, Object> redisTemplateJackson) {
        return new RedisUtil(redisTemplateJackson);
    }

    /**
     * 字符串序列化
     * <ol>
     *     <li>StringRedisSerializer</li>
     *     <li>
     *         GenericToStringSerializer: 相比于StringRedisSerializer,
     *         多了一个Converter。Spring启动时初始化一批内置的Converter，如果想将未设置Converter的Java对象类型转为String,需要设置对应的Converter
     *     </li>
     * </ol>
     *
     * @return
     */
    private RedisSerializer<String> stringRedisSerializer() {
        return RedisSerializer.string();
    }

    /**
     * JSON序列化
     * Redis使用Json序列化常用的的有：Jackson2JsonRedisSerializer和GenericJackson2JsonRedisSerializer，当然，fastJson也提供了实
     * 现的FastJsonRedisSerializer
     * <p>
     * GenericJackson2JsonRedisSerializer
     * 相比于Jackson2JsonRedisSerializer，构造方法多了一个ObjectMapper，如果是同一个ObjectMapper，两种方式并没有什么区别
     *
     * <p>
     * 1、使用Jackson2JsonRedisSerializer，通过设置ObjectMapper的属性，同样可以实现和Generic~一样的效果。GenericJackson2JsonRedisSerializer
     * 的构造方法GenericJackson2JsonRedisSerializer(java.lang.String)，就是在序列化结果中加入实体类全限定名的配置，<b>这样就可以直接使用类型转换。</b>
     * <br>
     * 指定序列化输入的类型：objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
     * 类必须是非final修饰的，如果是final修饰的类，比如String,Integer等会跑出异常。如果不指定输入的类型，redis Api获取的数据java解析出来后
     * 是一个LinkedHashMap的结构，需要自行解析。
     * <br>（参考：https://www.cnblogs.com/better-farther-world2099/articles/17024137.html），以下是一个案例：
     * <blockquote>
     * <pre>User o1 = om.convertValue(o, new TypeReference<User>() {});
     * </pre>
     * </blockquote>
     *
     * <p>
     *
     * Jackson2JsonRedisSerializer存储在redis中的数据示例：
     * <br>
     * 不指定输入类型："{\"id\":1001,\"name\":\"nancy\"}"
     * <br>
     * 指定输入类型：
     * <br>
     * 单个对象：
     * "[\"com.xkcoding.use.redis.entity.User\",{\"id\":1001,\"name\":\"nancy\"}]"
     * <br>
     * 数组类对象:
     * "[\"java.util.Arrays$ArrayList\",[[\"com.xkcoding.use.redis.entity.User\",{\"id\":1002,\"name\":\"kotlin\"}],
     * [\"com.xkcoding.use.redis.entity.User\",{\"id\":1003,\"name\":\"lucy\"}]]]"
     *
     * <br>
     * 如果enableDefaultTyping方法过期，不同版本的objetMapper可尝试使用下面的两种方式，参考：https://www.cnblogs.com/exmyth/p/13794524.html:
     * <blockquote><pre>
     *     objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
     *                  ObjectMapper.DefaultTyping.NON_FINAL,JsonTypeInfo.As.PROPERTY);
     * </pre></blockquote>
     * or
     * <blockquote><pre>
     *     objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator()
     *                  ,ObjectMapper.DefaultTyping.NON_FINAL);
     * </pre></blockquote>
     *
     * <p>使用GenericJackson2JsonRedisSerializer，存储在redis中的数据格式：
     * "[\"java.util.Arrays$ArrayList\",[{\"@class\":\"com.xkcoding.use.redis.entity.User\",\"id\":1002,
     * \"name\":\"kotlin\"},{\"@class\":\"com.xkcoding.use.redis.entity.User\",\"id\":1003,\"name\":\"lucy\"}]]"
     *
     *
     * <p>
     * 2、使用GenericJacksonRedisSerializer比Jackson2JsonRedisSerializer效率低，占用内存高。
     * 如果反序列化带泛型的数组类会报转换异常，解决办法存储以JSON字符串存储。
     */
    private RedisSerializer<Object> jackson2JsonSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
            new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 指定序列化输入的类型
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;

        // 如果使用默认的ObjectMapper，可以直接使用GenericJackson2JsonRedisSerializer
        // return RedisSerializer.json();
    }

    /**
     * JDK序列化：默认序列化规则，Java提供的序列化方式，效率高，占用空间少，可视化性差。
     * 使用场景: 使用字符串序列化和JSON序列化，反序列化的时候强依赖于对象是否由标准的构造方法，对于不符合这种标准的对象，使用JDK序列化，
     * 缺点是无法直观的在redis ui界面上查看数据
     *
     * @return
     */
    private RedisSerializer<Object> jdkSerializer() {
        return new JdkSerializationRedisSerializer();
    }

}
