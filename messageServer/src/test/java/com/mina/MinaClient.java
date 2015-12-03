package com.mina;

import com.Application;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MinaClient {
    private Application application;

    @Before
    public void init() throws Exception {
//        ApplicationContext context =
//                new AnnotationConfigApplicationContext(Application.class);
//        application = (Application) context.getBean("application");

        MinaMessage message = new MinaMessage();
        message.setId("1");
        message.setFrom("chenxi");
        message.setTo("wangyao");
        message.setType(MessageType.TEXT);
    }

    @Test
    public void test(){
        System.out.println("test");
    }

    @After
    public void after(){
        Assert.assertEquals(1,1);
    }
}
