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

@Configuration
@EnableIntegration
public class ScatterGatherTest {


    private static String transform(Object m) {

        return "recipient1 Response";
    }

    @Bean
  public IntegrationFlow scatterGatherflow(){

    return flow->flow.log()
        .scatterGather(s->s.applySequence(true).requiresReply(true)
            .recipient("transformer.input")
            .recipient("scatterGatherInnerflow.input")
                , g->g.outputProcessor(this::processGroup)
                , sg->sg.errorChannel("scatterGatherErrorChannel").gatherTimeout(1000)
        )
        .log()
        .bridge();
  }

  @Bean IntegrationFlow transformer(){
    return flow->flow.log()
        .transform(ScatterGatherTest::transform);
  }



  public String processGroup(MessageGroup group){
    String result=null;
    for(Message message: group.getMessages()) {

      if (message.getPayload() instanceof String) {
        result = (String) message.getPayload();
      }
    }

    return result;
  }


  @Bean
  public IntegrationFlow scatterGatherInnerflow(){
    return f->f
        .scatterGather(s->s.applySequence(true).requiresReply(true)
            .recipient("transformer.input")
            .recipient("scatterGatherTransformer.input"), null
                , sg->sg.errorChannel("scatterGatherErrorChannel").gatherTimeout(1000)
        );
  }

  @ServiceActivator(inputChannel = "scatterGatherErrorChannel")
  public Message<?> processAsyncScatterError(MessagingException payload) {
    return MessageBuilder.withPayload(payload.getCause().getCause())
            .copyHeaders(payload.getFailedMessage().getHeaders())
            .build();
  }

  @Bean
  public IntegrationFlow scatterGatherTransformer(){
    return f->f
        .transform(this::MessageTransformer);
  }

  public String MessageTransformer(String msg){
    if ("Test".equals(msg)||"Test1".equals(msg)){
      return msg;
    }else {
        return "other";
    }
  }

}
