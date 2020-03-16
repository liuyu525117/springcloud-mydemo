package com.liuyu.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.liuyu.model.User;

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @date 16/4/15 下午3:19.
 * @blog http://blog.didispace.com
 */
@Configuration
public class RedisConfig {
	
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;

	/**
	 * demo里面这个有问题
	 * @return
	 */
//    @Bean
//    JedisConnectionFactory jedisConnectionFactory() {
//        return new JedisConnectionFactory();
//    }

    @Bean
    public RedisTemplate<String, User> redisTemplate() {
        RedisTemplate<String, User> template = new RedisTemplate<String, User>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }
    
    @Bean
    public RedisTemplate<String, String> redisTemplate2() {
    	RedisTemplate<String, String> template = new RedisTemplate<String, String>();
    	template.setConnectionFactory(jedisConnectionFactory);
    	return template;
    }


}
