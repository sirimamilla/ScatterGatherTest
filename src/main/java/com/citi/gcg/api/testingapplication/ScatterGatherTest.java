package com.citi.gcg.api.testingapplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

import java.util.concurrent.Executors;

@Configuration
@EnableIntegration
public class ScatterGatherTest {


  @Bean
  public IntegrationFlow scatterGatherflow(){
    return flow->flow.log()
        .scatterGather(s->s.applySequence(true).requiresReply(true)
            .recipient("transformer.input")
            .recipient("scatterGatherInnerflow.input"))
        .log()
        .bridge();
  }

  @Bean IntegrationFlow transformer(){
    return flow->flow.log()
        .transform(m->"recipient1 Response");
  }

  public String processGroup(MessageGroup group){
    String result=null;
    for(Message message: group.getMessages()) {
      if ("Test1".equals(message.getPayload())) {
        throw new ClassCastException("Intentional Error Thrown!!!");
      }
      result = (String) message.getPayload();
    }

    return result;
  }


  @Bean
  public IntegrationFlow scatterGatherInnerflow(){
    return f->f.channel(channels -> channels.executor(Executors.newWorkStealingPool()))
        .scatterGather(s->s.applySequence(true).requiresReply(true)
            .recipient("transformer.input")
            .recipient("scatterGatherTransformer.input"));
  }



  @Bean
  public IntegrationFlow scatterGatherTransformer(){
    return f->f.channel(channels -> channels.executor(Executors.newWorkStealingPool()))
        .transform(this::MessageTransformer);
  }

  public String MessageTransformer(String msg){
    if ("Test".equals(msg)||"Test1".equals(msg)){
      return msg;
    }else {
      throw new RuntimeException("Throw Error");
    }
  }

}
