package com.example.sample2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


/**
 * @author LuckyMan
 */
@Configuration
public class CorrectChannelsConfig {
    private final DataTypeTransformer transformer;

    @Autowired
    public CorrectChannelsConfig(DataTypeTransformer transformer) {
        this.transformer = transformer;
    }

    @Bean

    public MessageChannel typedDataChannel() {
        return MessageChannels.queue().datatype(Integer.class).get();
    }

    @Bean
    public IntegrationFlow sampleTypedData(@Qualifier("typedDataChannel") MessageChannel input) {
        return f -> f.channel(input)
                .enrichHeaders(h -> h.headerFunction("sampleHeader", Message::getPayload))
                .transform(transformer)
                .channel("sendMailChannel");
    }

    @Service
    public static class DataTypeTransformer {

        @Transformer
        public String transform(Message<?> message) {
            return ((Integer) message.getHeaders().get("sampleHeader")) % 2 == 0
                    ? "Odd" : "Even";
        }
    }

    @MessagingGateway
    public interface SendMessageGateway {
        @Gateway(requestChannel = "typedDataChannel")
        void send(@Payload Integer messageBody, @Header("target") String targetEmail);
    }
}
