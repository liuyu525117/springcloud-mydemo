package com.liuyu.redis.lock.demo;

import redis.clients.jedis.Jedis;

public class RedisConnection {

	private Jedis jedis = new Jedis("192.168.0.100",6379);

	public Jedis getInstanceJedis() {
		return jedis;
	}

}
