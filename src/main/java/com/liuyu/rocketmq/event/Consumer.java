/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liuyu.rocketmq.event;

import java.util.List;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;


/**
 * Consumer，订阅消息
 */
/**
 * Consumer，订阅消息
 */
public class Consumer {

    public static void main(String[] args) throws InterruptedException, MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_4");
        consumer.setNamesrvAddr("192.168.0.99:9876;192.168.0.98:9876");
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("sendTest", "*"); //*代表不过滤
        
        /**
         * 先启动consumer,设置了批量消费，实际上还是一条消息消费；先启动producer,可以实现批量消费
         * 实际生产环境，一般都启动consumer
         */
        consumer.setConsumeMessageBatchMaxSize(10); 

        consumer.registerMessageListener(new MessageListenerConcurrently() {
        	
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,//直接批量的消费，实际上是一条一条消费
                    ConsumeConcurrentlyContext context) {
              
            	//System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs);
            	System.out.println("消息条数:  " + msgs.size()); //只有一条消息
            	MessageExt msg = msgs.get(0);
            	try {
	            	//for(MessageExt msg : msgs){
	            		String topic = msg.getTopic();
	            		String msgBody = new String(msg.getBody(),"utf-8");
	            		String tags = msg.getTags();
	            		System.out.println("收到消息  " + " topic: " + topic + ", msgBody: " + msgBody + ", tags:  " + tags);
	            		
	            		//一定要注意先启动consumer，也就是先订阅
	            		if("Hello RocketMQ 4".equals(msgBody)){
	            			System.out.println("==========失败消息开始==========");
	            			System.out.println(msg);
	            			System.out.println(msgBody);
	            			System.out.println("==========失败消息结束==========");
	            			
	            			//异常
//	            			int i = 1/0;
	            		}
	            	//}
            	} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(msg.getReconsumeTimes() == 2){
						//记录日志
						System.out.println("重试两次，还是没有成功，记录日志");
						return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					}
					//1s 2s 5s ............ 2h
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;//出现异常，稍后发送
				}
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; //返回消费成功标志
            }
        });

        consumer.start();

        System.out.println("Consumer Started.");
    }
}
