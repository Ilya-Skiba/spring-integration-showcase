package com.example.sample2;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.ftp.Ftp;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.messaging.MessageChannel;

import java.io.File;

/**
 * Created by LuckyMan on 02.11.2016.
 */
@Configuration
public class FtpConfig {

    @Bean
    DefaultFtpSessionFactory ftpSessionFactory() {
        DefaultFtpSessionFactory defaultFtpSessionFactory = new DefaultFtpSessionFactory();
        defaultFtpSessionFactory.setHost("localhost");
        defaultFtpSessionFactory.setUsername("user");
        defaultFtpSessionFactory.setPassword("password");
        defaultFtpSessionFactory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
        return defaultFtpSessionFactory;
    }

    @Bean
    public MessageChannel ftpInboundResultChannel() {
        return MessageChannels.queue().get();
    }


    @Bean
    public IntegrationFlow ftpInboundFlow() {
        return IntegrationFlows
                .from(Ftp.inboundAdapter(ftpSessionFactory())
                        .preserveTimestamp(true)
                        .localDirectory(new File("tmp"))
                        .autoCreateLocalDirectory(true))
                .channel("ftpInboundResultChannel")
                .get();
    }

    @Bean
    public IntegrationFlow processDownloaded() {
        return IntegrationFlows
                .from("ftpInboundResultChannel")
                .transform(Transformers.fileToString())
                .<String, String>route(s -> s.split(":")[0],
                        spec -> spec.prefix("send").suffix("Channel")
                                .channelMapping("mail", "RawMail")
                                .channelMapping("tweet", "Tweet")
                                .resolutionRequired(false)
                                .defaultOutputChannel("sendSystemOutChannel"))
                .get();
    }

    @Bean
    public IntegrationFlow showOnConsole() {
        return f -> f.channel("sendSystemOutChannel")
                .handle(System.out::println);
    }

    @Bean
    public IntegrationFlow eMail() {
        return f -> f.channel("sendRawMailChannel")
                .enrichHeaders(h -> h.<String>headerFunction("target", m -> m.getPayload().split(":")[1]))
                .<String, String>transform(s -> s.substring(s.lastIndexOf(":")))
                .channel("sendMailChannel");
    }
}
