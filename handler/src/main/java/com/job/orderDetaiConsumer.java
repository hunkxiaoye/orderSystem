package com.job;

import com.common.Elasticsearch.EsWriteUtils;
import com.common.constantCode;
import com.common.kafka.AbstractConsumer;
import com.common.kafka.annotation.KafkaConf;
import com.db.model.orderDetail;
import com.db.service.inf.iorderDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@KafkaConf(topic = "order_detail_create", groupid = "test_yp", threads = 1)
public class orderDetaiConsumer extends AbstractConsumer<orderDetail> {
    protected static final Logger log = LoggerFactory.getLogger(orderDetaiConsumer.class);
    @Autowired
    private EsWriteUtils esWriteUtils;
    @Autowired
    private iorderDetailService service;
    protected boolean process(orderDetail msg) {

        try {
            //插入数据库
            addOrderDetail(msg);
            //添加es索引
            esWriteUtils.addIndex(constantCode.getClusterName(),msg);
        } catch (Exception e) {
            log.error("错误："+e);
        }
        return false;
    }


    private Integer addOrderDetail(orderDetail model){

        return service.add(model);
    }
}
