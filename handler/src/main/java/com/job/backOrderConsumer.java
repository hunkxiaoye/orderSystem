package com.job;

import com.common.Elasticsearch.EsWriteUtils;
import com.common.constantCode;
import com.common.kafka.AbstractConsumer;
import com.common.kafka.KafkaProducers;
import com.common.kafka.annotation.KafkaConf;
import com.db.model.*;
import com.db.service.inf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@KafkaConf(topic = "back_order", groupid = "test_yp", threads = 1)
public class backOrderConsumer extends AbstractConsumer<backOrder> {
    protected static final Logger log = LoggerFactory.getLogger(backOrderConsumer.class);
    @Autowired
    private EsWriteUtils esWriteUtils;
    @Autowired
    private ibackOrderService service;
    @Autowired
    private igoodsService goodservice;
    @Autowired
    private iorderPayService orderpayservice;
    @Autowired
    private iorderInfoService orderinfoservice;
    @Autowired
    private iorderDetailService detailService;
    @Autowired
    private KafkaProducers producers;

    protected boolean process(backOrder msg) {

        try {
            //插入数据库
            addBackOrder(msg);
            orderInfo info = orderinfoservice.findByorderid(msg.getOrder_number());
            orderPay pay = orderpayservice.findbyorderid(msg.getOrder_number());
            info.setOrder_type(3);
            //更新订单状态
            int isok = orderinfoservice.update(info);

            //恢复库存
            orderDetail detail = detailService.findbyid(info.getId()).get(0);
            if (detail.getIs_operating() == 0) {
                stockModel stockModel = new stockModel();
                //此demo每次只有一个商品
                stockModel.setStock(1);
                stockModel.setId(detail.getGoods_id());
                //恢复库存
                goodservice.returnUpdate(stockModel);

                detail.setIs_operating(1);
                detailService.update(detail);

                //退款入参
                refundModel model = new refundModel();
                model.setOrder_pay_number(pay.getOrder_pay_number());
                model.setBack_number(msg.getBack_number());
                model.setOrder_number(msg.getOrder_number());
                model.setAmount(msg.getAmount());
                producers.send("refund_order", model);

            }

        } catch (Exception e) {
            log.error("错误："+e);
        }
        return false;
    }


    private Integer addBackOrder(backOrder model){

        return service.add(model);
    }

}
