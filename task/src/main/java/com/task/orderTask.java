package com.task;

import com.common.kafka.KafkaProducers;
import com.db.model.*;
import com.db.service.inf.ibackOrderService;
import com.db.service.inf.igoodsService;
import com.db.service.inf.iorderDetailService;
import com.db.service.inf.iorderInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class orderTask {

    private static final Logger log = LoggerFactory.getLogger(orderTask.class);
    @Autowired
    private iorderInfoService service;
    @Autowired
    private iorderDetailService iorderDetailService;
    @Autowired
    private igoodsService igoodsService;
    @Autowired
    private ibackOrderService backOrder;
    @Autowired
    private KafkaProducers producers;

    @Scheduled(fixedDelay = 1000 * 60 * 15) //* 15
    public void orderTimeOut() {
        log.info("取消超时订单开始");
        orderInfo model;
        orderDetail orderdetail;
        stockModel stockmodel = new stockModel();
        List<orderInfo> list = service.findByStatus(0);
        try {
            for (int i = 0; i < list.size(); i++) {
                model = list.get(i);
                model.setOrder_type(6);
                model.setPay_status(3);
                model.setUpdate_time(LocalDateTime.now());
                service.update(model);
                List<orderDetail> orderDetailList = iorderDetailService.findbyid(model.getId());
                //恢复可用库存 恢复锁定库存
                for (int j = 0; j < orderDetailList.size(); j++) {
                    orderdetail = orderDetailList.get(j);
                    stockmodel.setStock(orderdetail.getAmount());
                    stockmodel.setId(orderdetail.getGoods_id());
                    igoodsService.returnUpdate(stockmodel);
                    orderdetail.setIs_operating(1);
                    orderdetail.setUpdate_time(LocalDateTime.now());
                    iorderDetailService.update(orderdetail);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("取消超时订单结束");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 2)
    public void backOrder() {
        log.info("退单补偿作业开始");
        List<backOrder> list = backOrder.findByStatus(2);
        for (int i = 0; i < list.size(); i++) {
            //退款入参
            refundModel model = new refundModel();
            model.setOrder_pay_number(list.get(i).getOrder_pay_number());
            model.setBack_number(list.get(i).getBack_number());
            model.setOrder_number(list.get(i).getOrder_number());
            model.setAmount(list.get(i).getAmount());
            producers.send("refund_order", model);

        }
    }
}
