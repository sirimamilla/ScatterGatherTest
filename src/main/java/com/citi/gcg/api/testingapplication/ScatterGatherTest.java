package com.citi.gcg.api.testingapplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
@EnableIntegration
public class ScatterGatherTest {


    @Bean
    public IntegrationFlow scatterGatherflow() {

        return flow -> flow.log()
                .scatterGather(s -> s.applySequence(true).requiresReply(true)
                                .recipient("scatterGatherInnerflow.input")
                        , g -> g.outputProcessor(mg -> mg.getOne())
                        , sg -> sg.errorChannel("scatterGatherErrorChannel").gatherTimeout(1000)
                )
                .log()
                .bridge();
    }


    @Bean
    public IntegrationFlow scatterGatherInnerflow() {
        return f -> f
                .scatterGather(s -> s.applySequence(true).requiresReply(true)
                                .recipientFlow(IntegrationFlowDefinition::bridge).recipientFlow(IntegrationFlowDefinition::bridge)

                        , g -> g.outputProcessor(mg -> mg.getOne())
                        , sg -> sg.errorChannel("scatterGatherErrorChannel").gatherTimeout(1000)
                );
    }

    @ServiceActivator(inputChannel = "scatterGatherErrorChannel")
    public Message<?> processAsyncScatterError(MessagingException payload) {
        return MessageBuilder.withPayload(payload.getCause().getCause())
                .copyHeaders(payload.getFailedMessage().getHeaders())
                .build();
    }




}
