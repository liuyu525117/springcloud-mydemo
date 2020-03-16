package com.liuyu.web;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 解决高并发库存超卖问题
 * @author liuyu
 *
 */
@RestController
public class StockRedisController {
	
	@Autowired
	@Qualifier("redisTemplate2")
	private RedisTemplate<String, String> redisList;
	
	private static final long STOCK_NUM = 100; //库存数量
	
	private static AtomicInteger count = new AtomicInteger(0);
	
	/**
	 * 增加库存数量  先查库存表，将数据写入到redis中
	 * @param item
	 * @return
	 */
	@RequestMapping(value = "redis/addStock/{item}", method = RequestMethod.GET)
	String addStock(@PathVariable String item) {
		redisList.delete("goods1");
		for(int i=0 ; i< Integer.parseInt(item); i++) {
			redisList.opsForList().leftPush("goods1", "TV" + i);
		}
		return "增加了库存数量为：" + item;
	}
	
	/**
	 * 购买商品 redis解决方案
	 * @return
	 */
	@RequestMapping(value = "redis/buyGoods", method = RequestMethod.GET)
	String buyGoods() {
		ListOperations<String, String> opsList = redisList.opsForList();
		//检查库存
//		long stockRemain = opsList.size("goods1");
//		long actualStock = STOCK_NUM
		String goods = redisList.opsForList().rightPop("goods1");
		if(StringUtils.isEmpty(goods)) {
			return "商品已经被抢购完了，下次再光临！";
		}
		redisList.opsForList().leftPush("order:1","user" + count.incrementAndGet());
		System.out.println("==========购买商品为： ======"  + goods);
		//更新数据库 库存表的数量
		return "您已下单成功 ，商品为： " + goods;
	}
	
	/**
	 * 乐观锁
	 * @return
	 */
	String buyGoodsOpt() {
		/**
		 * 1、添加第3个字段version，int类型，default值为0。version值每次update时作加1处理。
		 * ALTER TABLE table ADD COLUMN version INT DEFAULT '0' NOT NULL AFTER use_count; 
		 * 
		 * 2、SELECT时同时获取version值（例如为3）。
		 * SELECT use_count, version FROM table WHERE id=123456 AND use_count < 1000;
		 * 
		 * 3、UPDATE时检查version值是否为第2步获取到的值。
		 * UPDATE table SET version=4, use_count=use_count+1 WHERE id=123456 AND version=3;
		 */
		
		return null;
	}
	
	String buyGoodsPess() {
		/**
		 * //0.开始事务
			begin;/begin work;/start transaction; (三者选一就可以)
			//1.查询出商品信息
			select status from t_goods where id=1 for update;
			//2.根据商品信息生成订单
			insert into t_orders (id,goods_id) values (null,1);
			//3.修改商品status为2
			update t_goods set status=2;
			//4.提交事务
			commit;/commit work;
		 */
		return null;
	}

}
