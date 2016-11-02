package com.example;

import com.example.sample2.CorrectChannelsConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by LuckyMan on 30.10.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailDescriptionTest {
    @Autowired
    private CorrectChannelsConfig.SendMessageGateway gateway;

    @Test
    public void test() throws Exception {
        gateway.send(3, "app.si@yopmail.com");
        Thread.sleep(30000);
    }
}
