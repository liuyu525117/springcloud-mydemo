package com.liuyu.redis.lock.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实际应用实现redis分布式加锁
 * @author liuyu
 *
 */
@RestController
public class LockRedisController {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private static String KEY_PRE = "REDIS_LOCK_";

	private DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	@RequestMapping(value = "redis/lock", method = RequestMethod.GET)
	public String testLockApplication() {
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				String key = "20171228";
		        String value = tryLock(key);
		        try {
		            if (value != null) {
		                System.out.println(Thread.currentThread().getName() + " lock key = " + key + " success! ");
		                Thread.sleep(5 * 1000);
		            } else {
		                System.out.println(Thread.currentThread().getName() + " lock key = " + key + " failure! ");
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            if (value == null) {
		                value = "";
		            }
		            System.out.println(Thread.currentThread().getName() + " unlock key = " + key + " " + unLock(key, value));

		        }
			}).start();
		}
		return "success";
	}

	private String tryLock(String key) {
		try {
	        key = KEY_PRE + key;
	        String value = fetchLockValue();
	        // 这里要保证原子性
	        if (stringRedisTemplate.opsForValue().setIfAbsent(key, value)) {
	        	//设置key的过期时间
	        	stringRedisTemplate.expire(key, 20, TimeUnit.SECONDS);
        	   System.out.println("Reids Lock key : " + key + ",value : " + value);
	           return value;
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解锁要实现原子性
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean unLock(String key, String value) {
	    Long RELEASE_SUCCESS = 1L;
        key = KEY_PRE + key;
		DefaultRedisScript<Long> script = new DefaultRedisScript<Long>();
        String command = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
		script.setScriptText(command);
		script.setResultType(Long.class); //多余指定了
		if (RELEASE_SUCCESS.equals(stringRedisTemplate.execute(script, Collections.singletonList(key), value))) {
            return true;
		}
		return false;
	}

	/**
	 * 生成加锁的唯一字符串
	 *
	 * @return 唯一字符串
	 */
	private String fetchLockValue() {
	    return UUID.randomUUID().toString() + "_" + df.format(new Date());
	}
}
