package com.job;

import com.common.Elasticsearch.EsWriteUtils;
import com.common.constantCode;
import com.common.kafka.AbstractConsumer;
import com.common.kafka.annotation.KafkaConf;
import com.db.model.*;
import com.db.service.inf.ibackOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@KafkaConf(topic = "back_order", groupid = "test_yp", threads = 1)
public class backOrderConsumer extends AbstractConsumer<backOrder> {
    protected static final Logger log = LoggerFactory.getLogger(backOrderConsumer.class);
    @Autowired
    private EsWriteUtils esWriteUtils;
    @Autowired
    private ibackOrderService service;

    protected boolean process(backOrder msg) {

        try {
            //插入数据库
            addBackOrder(msg);
            //添加es索引
            esWriteUtils.addIndex(constantCode.getClusterName(),msg);
        } catch (Exception e) {
            log.error("错误："+e);
        }
        return false;
    }


    private Integer addBackOrder(backOrder model){

        return service.add(model);
    }

}
