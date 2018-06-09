package com.liuyu.web;

import java.util.Date;

import javax.sound.midi.Sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局id生成器  后期可以优化
 * 存在问题，counter数字过大后，无法保证时间不重复
 * @author liuyu
 *
 */
@RestController
public class GeneratorIdRedisController {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@RequestMapping(value = "redis/generateId", method = RequestMethod.GET)
	public String generateId() {
		RedisAtomicLong counter = new RedisAtomicLong("0", redisTemplate.getConnectionFactory());
		return "CB" + new Date().getTime() + String.valueOf(counter.incrementAndGet());
	}
}
