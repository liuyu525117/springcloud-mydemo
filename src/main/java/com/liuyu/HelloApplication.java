package com.liuyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * springboot整合mybatis
 * ref: http://blog.csdn.net/saytime/article/details/74783296
 * @author liuyu
 *
 */
@EnableHystrix
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.liuyu.mapper")
//@EnableConfigurationProperties(RocketmqProperties.class)
public class HelloApplication {

	//测试git用
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

}
