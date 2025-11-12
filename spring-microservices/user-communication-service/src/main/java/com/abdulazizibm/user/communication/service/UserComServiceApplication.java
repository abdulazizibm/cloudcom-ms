package com.abdulazizibm.user.communication.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Import(com.abdulazizibm.common.client.AwsSqsClient.class)
@EnableScheduling
@SpringBootApplication
public class UserComServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserComServiceApplication.class, args);
  }

}
