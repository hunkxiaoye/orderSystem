package com.job;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

@Component
public class HandlerJob implements InitializingBean, ServletContextAware {

    @Autowired
    private orderConsumer consumer;
    @Autowired
    private orderDetaiConsumer detaiConsumer;
    public void Start() {

        try {
            System.out.println("订单创建开始");
            consumer.start();
            detaiConsumer.start();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void setServletContext(ServletContext servletContext) {
        try {
            Start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void afterPropertiesSet() throws Exception {
    }
}
