package com.spring.boot.learning.springbootcloudconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;



@EnableConfigServer
@SpringBootApplication
public class SpringBootCloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCloudConfigServerApplication.class, args);
	}

}
