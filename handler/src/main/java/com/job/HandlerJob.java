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
    private backOrderConsumer backConsumer;
    @Autowired
    private orderPayConsumer payConsumer;
    @Autowired
    private refundConsumer refundconsumer;
    @Autowired
    private orderDetaiConsumer detaiConsumer;
    public void Start() {

        try {
            System.out.println("订单创建开始");
            consumer.start();
            System.out.println("订单支付详情创建开始");
            detaiConsumer.start();
            System.out.println("退款订单开始");
            backConsumer.start();
            System.out.println("支付创建开始");
            payConsumer.start();
            //refundconsumer.start();
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
