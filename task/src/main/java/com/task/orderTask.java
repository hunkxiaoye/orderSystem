package com.task;

import com.db.model.orderDetail;
import com.db.model.orderInfo;
import com.db.model.stockModel;
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

    @Scheduled(fixedDelay = 1000 * 60 * 3) //* 15
    public void orderTimeOut() {
        log.info("取消超时订单开始");
        orderInfo model;
        orderDetail orderdetail ;
        stockModel stockmodel =new stockModel();
        List<orderInfo> list = service.findByStatus(0);
        try {
            for (int i = 0; i < list.size(); i++) {
                model =list.get(i);
                model.setOrder_type(6);
                model.setPay_status(3);
                model.setUpdate_time(LocalDateTime.now());
                service.update(model);
                List<orderDetail> orderDetailList = iorderDetailService.findbyid(model.getId());
                //恢复可用库存 恢复锁定库存
                for (int j =0;j<orderDetailList.size();j++){
                    orderdetail =orderDetailList.get(j);
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
}
