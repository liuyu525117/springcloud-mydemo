package com.liuyu.rocketmq.listen;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.liuyu.rocketmq.event.RocketMqEvent;

/**
 * @Author:钟志苗
 * @Date: 8:35 2017/5/13 
 * @Description:消费监听器
 */
@Component
public class ConsumerListen {

//	@Autowired
//	private TUserService tUserService;

	@EventListener(condition = "#event.topic=='test'")
//	@EventListener(condition = "#event.topic=='TopicTest1' && #event.tag=='TagA'") 
	public void testListen(RocketMqEvent event) {
		DefaultMQPushConsumer consumer = event.getConsumer();
		try {
			String id = new String(event.getMessageExt().getBody(),"utf-8");
//			Long sid = Long.valueOf(id);
//			TUser tUser = new TUser();
//			tUser.setId(sid);
//			System.out.println("bl"+tUserService.saveTUser(tUser));
			System.out.println("======收到消息了======： " + id);
		} catch (Exception e) {
			e.printStackTrace();
			if (event.getMessageExt().getReconsumeTimes() <= 1) {// 重复消费1次
				try {
					consumer.sendMessageBack(event.getMessageExt(), 1);
				} catch (RemotingException | MQBrokerException | InterruptedException | MQClientException e1) {
					e1.printStackTrace();
					//消息进行定时重试
				}
			} else {
				System.out.println("消息消费失败，定时重试");
			}
		}
	}

	@EventListener(condition = "#event.topic=='order'")
	public void normalListen(RocketMqEvent event) {
		try {
			System.out.println("顺序消息：" + new String(event.getMessageExt().getBody(),"utf-8"));
		}catch (Exception e){
			e.printStackTrace();
		}

	}
}
