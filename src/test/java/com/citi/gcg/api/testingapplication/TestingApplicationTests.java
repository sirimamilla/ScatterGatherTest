package com.citi.gcg.api.testingapplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = TestingApplication.class)
public class TestingApplicationTests {


  @Test
  public void ScatterGatherTestSuccess(){

    String msg=scatterGatherService.scatterGatherFlow("Test");
    System.out.println(msg);

  }
  @Test
  public void ScatterGatherTestError(){

    System.out.println("Sending Request that would cause error");
    try{
      scatterGatherService.scatterGatherFlow("Error");
    }catch (Exception e){
      e.printStackTrace();
    }
  }


  @Test
  public void ScatterGatherInnerTestError(){

    System.out.println("Sending Request that would cause error");
    try{
      scatterGatherService.scatterGatherInnerflow("Error");
    }catch (Exception e){
      e.printStackTrace();
    }


  }

  @Autowired ScatterGatherService scatterGatherService;

  @MessagingGateway
  public interface ScatterGatherService{
    @Gateway(requestChannel = "scatterGatherflow.input")
    public String scatterGatherFlow(String msg);

    @Gateway(requestChannel = "scatterGatherInnerflow.input")
    public String scatterGatherInnerflow(String msg);
  }
}
