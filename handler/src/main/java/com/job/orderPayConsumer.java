package com.job;

import com.common.Elasticsearch.EsWriteUtils;
import com.common.constantCode;
import com.common.kafka.AbstractConsumer;
import com.common.kafka.annotation.KafkaConf;
import com.db.model.orderPay;
import com.db.service.inf.iorderPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@KafkaConf(topic = "order_pay_create", groupid = "test_yp", threads = 1)
public class orderPayConsumer extends AbstractConsumer<orderPay> {
        protected static final Logger log = LoggerFactory.getLogger(orderPayConsumer.class);
        @Autowired
        private EsWriteUtils esWriteUtils;
        @Autowired
        private iorderPayService service;
        protected boolean process(orderPay msg) {

            try {
                //插入数据库
                addOrderpay(msg);
                //添加es索引
                esWriteUtils.addIndex(constantCode.getClusterName(),msg);
            } catch (Exception e) {
                log.error("错误："+e);
            }
            return false;
        }


        private Integer addOrderpay(orderPay model){

            return service.add(model);
        }
    }

