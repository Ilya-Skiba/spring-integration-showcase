package com.example;

import com.example.sample1.SampleIntegrationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoProcessorApplicationTests {

    @Autowired
    private SampleIntegrationConfig.InputGateway inputGateway;

    @Test
    public void contextLoads() throws Exception {
        inputGateway.run("Hello, world");
    }

    @Test
    public void contextAsync() throws Exception {
        inputGateway.runAsync(2).get();
    }

    @Test(timeout = 3000)
    public void contextAsyncFailure() throws Exception {
        inputGateway.runAsync(5).get();
    }

}
