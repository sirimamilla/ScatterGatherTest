package com.citi.gcg.api.testingapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.citi.gcg.api.testingapplication")
public class TestingApplication {

  public static void main(String[] args){
    SpringApplication.run(TestingApplication.class, args);
  }
}
