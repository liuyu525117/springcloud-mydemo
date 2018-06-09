package com.liuyu.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liuyu.model.User;

@RestController
public class CommonRedisController {

	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisTemplate<String, User> redisTemplate;
	
	@Autowired
	private RedisTemplate<String, String> redisList;
	
	/**
	 * String类型处理
	 * @return
	 */
	@RequestMapping(value = "redis/str", method = RequestMethod.GET)
	public String getStrRedis() {
		stringRedisTemplate.opsForValue().set("aaa", "111");
		String result = stringRedisTemplate.opsForValue().get("aaa");
		return result;
	}
	
	/**
	 * 保存对象
	 * 保存在redis端也是string类型，序列化了
	 * @return
	 */
	@RequestMapping(value = "redis/user", method = RequestMethod.GET)
	public User getUserFromRedis() {
		// 保存对象
		User user = new User("man", 20);
		redisTemplate.opsForValue().set(user.getUsername(), user);
		User returnUser = redisTemplate.opsForValue().get("man");
		return returnUser;
	}
	
	/**
	 * list数据使用
	 * @return
	 */
	@RequestMapping(value = "redis/test", method = RequestMethod.GET)
	public String testHighCocurrency() {
		redisList.opsForList().range("goods1", 0, -1);
		for(int i=0 ; i<100;i++) {
			redisList.opsForList().leftPush("goods1", "TV" + i);
		}
		return "success";
	}
}
