package com.example.sample2;

import com.example.sample2.transform.StringToMimeTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.GenericMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by s.bilida on 10.03.2016.
 */
@Configuration
public class MailConfig {

    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.protocol}")
    private String protocol;
    @Value("${mail.user}")
    private String user;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.encoding}")
    private String encoding;
    @Value("${mail.smtp.auth}")
    private boolean smtpAuth;
    @Value("${mail.smtp.starttls.enable}")
    private boolean smtpStartTlsEnable;
    @Value("${mail.debug}")
    private boolean debug;
    @Autowired
    private StringToMimeTransformer toMimeTransformer;

    @Bean
    public MessageChannel sendMailChannel() {
        return MessageChannels.queue().datatype(String.class).get();
    }

    @Bean
    public JavaMailSenderImpl mailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    IntegrationFlow sendMailFlow() {
        return IntegrationFlows.from("sendMailChannel")
                .enrichHeaders(h -> h.header(MailHeaders.FROM, from))
                .transform(toMimeTransformer)
                .handle(Mail.outboundAdapter(host)
                                .port(port)
                                .protocol(protocol)
                                .credentials(user, password)
                                .defaultEncoding(encoding)
                                .javaMailProperties(p -> {
                                    p.put("mail.smtp.auth", smtpAuth);
                                    p.put("mail.smtp.starttls.enable", smtpStartTlsEnable);
                                    p.put("mail.debug", debug);
                                }),
                        e -> e.id("sendMailEndpoint")).get();
    }


    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    @Primary
    public PollerMetadata poller() {
        return Pollers.fixedRate(500).get();
    }

}

