package com.liuyu.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.liuyu.HelloApplication;
import com.liuyu.model.User;

import redis.clients.jedis.Jedis;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(HelloApplication.class)
public class RedisTests {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisTemplate<String, User> redisTemplate;
	
	private static Jedis jedis = new Jedis("192.168.0.100",6379);

	@Test
	public void test() throws Exception {

		// 保存字符串
		stringRedisTemplate.opsForValue().set("aaa", "111");
		Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));

		// 保存对象
		User user = new User("man", 20);
		redisTemplate.opsForValue().set(user.getUsername(), user);

		user = new User("蝙蝠侠", 30);
		redisTemplate.opsForValue().set(user.getUsername(), user);

		user = new User("蜘蛛侠", 40);
		redisTemplate.opsForValue().set(user.getUsername(), user);
		Assert.assertEquals(20, redisTemplate.opsForValue().get("man").getAge());
		Assert.assertEquals(30, redisTemplate.opsForValue().get("蝙蝠侠").getAge());
		Assert.assertEquals(40, redisTemplate.opsForValue().get("蜘蛛侠").getAge());

	}
	
	@Test
	public void testRedis() {
        Map<String, String> user = new HashMap<String, String>();
        user.put("name","huangyuxuan");
        user.put("age", "0.5");
        user.put("sex","男");
        jedis.hmset("user",user);
        List<String> rsmap = jedis.hmget("user", "name", "age","sex");
        System.out.println(rsmap);  
	}

}
