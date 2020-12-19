package com.liuyu.rocketmq.config;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionMQProducer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.liuyu.rocketmq.event.RocketMqEvent;

/**
 * 注意rocket client端和服务端版本保持一致，不然消息消费不了
 * @author liuyu
 *
 */
@Configuration
@EnableConfigurationProperties(RocketMqProperties.class)
public class RocketMqConfiguration {

	@Autowired
	private RocketMqProperties rmqProperties;

	@Autowired
	private ApplicationEventPublisher publisher;

	/**
	 * 发送普通消息
	 */
	@Bean(name = "default")
	public DefaultMQProducer defaultMQProducer() throws MQClientException {
		DefaultMQProducer producer = new DefaultMQProducer(rmqProperties.getDefaultProducer());
		producer.setNamesrvAddr(rmqProperties.getNamesrvAddr());
		producer.setInstanceName(rmqProperties.getInstanceName());
//		producer.setVipChannelEnabled(false);
		producer.start();
		System.out.println("DefaultMQProducer is Started.");
		return producer;
	}

	/**
	 * 发送事务消息
	 */
	@Bean(name = "trans")
	public TransactionMQProducer transactionMQProducer() throws MQClientException {
		TransactionMQProducer producer = new TransactionMQProducer(rmqProperties.getTransactionProducer());
		producer.setNamesrvAddr(rmqProperties.getNamesrvAddr());
		producer.setInstanceName(rmqProperties.getInstanceName());
		producer.setTransactionCheckListener((MessageExt msg) ->{
			System.out.println("事务回查机制！");
			return  LocalTransactionState.COMMIT_MESSAGE;
		});
		// 事务回查最小并发数
		producer.setCheckThreadPoolMinSize(2);
		// 事务回查最大并发数
		producer.setCheckThreadPoolMaxSize(5);
		// 队列数
		producer.setCheckRequestHoldMax(2000);
		producer.start();
		System.out.println("TransactionMQProducer is Started.");
		return producer;
	}

	/**
	 * 消费者
	 */
	@Bean
	public DefaultMQPushConsumer pushConsumer() throws MQClientException {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rmqProperties.getDefaultConsumer());
		Set<String> setTopic = rmqProperties.getDefaultTopic();
		for (String topic : setTopic) {
			consumer.subscribe(topic, "*");
		}
		consumer.setNamesrvAddr(rmqProperties.getNamesrvAddr());
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.setConsumeMessageBatchMaxSize(1);
		consumer.registerMessageListener((List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
				MessageExt msg = msgs.get(0);
				try {
					publisher.publishEvent(new RocketMqEvent(msg, consumer));
				} catch (Exception e) {
					if (msg.getReconsumeTimes() <= 1) {
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					} else {
						System.out.println("定时重试！");
					}
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});
		new Thread(() -> {
				try {
					Thread.sleep(3000);
					try {
						consumer.start();
						System.out.println("普通消费开启");
					} catch (MQClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}).start();
		return consumer;
	}

	/**
	 * 顺序消费者
	 */
	@Bean
	public DefaultMQPushConsumer pushOrderConsumer() throws MQClientException {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rmqProperties.getOrderConsumer());
		Set<String> setTopic = rmqProperties.getOrderTopic();
		for (String topic : setTopic) {
			consumer.subscribe(topic, "*");
		}
		consumer.setNamesrvAddr(rmqProperties.getNamesrvAddr());
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.setConsumeMessageBatchMaxSize(1);
		consumer.registerMessageListener((List<MessageExt> msgs, ConsumeOrderlyContext context) -> {
				MessageExt msg = msgs.get(0);
				try {
					publisher.publishEvent(new RocketMqEvent(msg, consumer));
				} catch (Exception e) {
					if (msg.getReconsumeTimes() <= 1) {
						return ConsumeOrderlyStatus.SUCCESS;
					} else {
						System.out.println("定时重试！");
					}
				}
				return ConsumeOrderlyStatus.SUCCESS;
		});
		/**
		 * 延迟5秒再启动，主要是等待spring事件监听相关程序初始化完成，否则，
		 * 回出现对RocketMQ的消息进行消费后立即发布消息到达的事件，然而此事件的监听程序还未初始化，从而造成消息的丢失
		 */
		new Thread(() ->{
				try {
					Thread.sleep(3000);
					try {
						consumer.start();
						System.out.println("顺序消费开启");
					} catch (MQClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}).start();
		return consumer;
	}

}
