package com.citi.gcg.api.testingapplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.concurrent.Executors;

@Configuration
@EnableIntegration
public class ScatterGatherTest {


  @Bean
  public IntegrationFlow scatterGatherflow(){
    return flow->flow.log()
        .scatterGather(s->s.applySequence(true).requiresReply(true)
            .recipient("transformer.input")
            .recipient("scatterGatherInnerflow.input"), g->g.outputProcessor(this::processGroup), sg->sg.errorChannel("scatterGatherErrorChannel"))
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
      if (message.getPayload() instanceof List) {
        if ("Test1".equals(((List<String>) message.getPayload()).get(1)))
          throw new ClassCastException("Intentional Error Thrown!!!");
      }
      if (message.getPayload() instanceof String) {
        result = (String) message.getPayload();
      }
    }

    return result;
  }


  @Bean
  public IntegrationFlow scatterGatherInnerflow(){
    return f->f.channel(channels -> channels.executor(Executors.newWorkStealingPool()))
        .scatterGather(s->s.applySequence(true).requiresReply(true)
            .recipient("transformer.input")
            .recipient("scatterGatherTransformer.input"), null, sg->sg.errorChannel("scatterGatherErrorChannel"));
  }

  @ServiceActivator(inputChannel = "scatterGatherErrorChannel")
  public Message<?> processAsyncScatterError(MessagingException payload) {
    return MessageBuilder.withPayload(payload.getCause().getCause())
            .copyHeaders(payload.getFailedMessage().getHeaders())
            .build();
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
