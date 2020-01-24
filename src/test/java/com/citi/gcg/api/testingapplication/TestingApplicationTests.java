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


    @Test(timeout = 3000)
    public void ScatterGather3rdLevelSuccess() {

        String msg = scatterGatherService.scatterGather3rdLevelflow("Test");
        System.out.println(msg);

    }

    @Test(timeout = 3000)
    public void ScatterGather2ndLevelSuccess() {

        String msg = scatterGatherService.scatterGatherFlow("Test");
        System.out.println(msg);

    }

    @Test(timeout = 3000)
    public void scatterGather1stLevelTestSuccess() {

        String msg = scatterGatherService.scatterGatherInnerflow("Test");
        System.out.println(msg);

    }


    @Autowired
    ScatterGatherService scatterGatherService;

    @MessagingGateway
    public interface ScatterGatherService {
        @Gateway(requestChannel = "scatterGatherflow.input")
        public String scatterGatherFlow(String msg);

        @Gateway(requestChannel = "scatterGatherInnerflow.input")
        public String scatterGatherInnerflow(String msg);

        @Gateway(requestChannel = "scatterGather3rdLevelflow.input")
        public String scatterGather3rdLevelflow(String msg);
    }
}
