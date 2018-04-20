package com.controller;

import com.common.CookieUtils;
import com.common.kafka.KafkaProducers;
import com.db.model.backOrder;
import com.db.model.orderInfo;
import com.db.model.orderPay;
import com.db.service.inf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class BackorderController {

    @Autowired
    private igoodsService service;
    @Autowired
    private iorderPayService orderpayservice;
    @Autowired
    private iorderInfoService orderinfoservice;
    @Autowired
    private iorderDetailService detailService;
    @Autowired
    private KafkaProducers producers;
    @Autowired
    private irestfulService restfulservice;

    @RequestMapping(value = "/cancelorder")
    public String cancelOrder(String orderid,Model model,HttpServletRequest request) throws Exception {

        orderInfo info = orderinfoservice.findByorderid(orderid);
        orderPay pay = orderpayservice.findbyorderid(orderid);
        int isok =0;
        //判断是否支付
        if (info.getPay_status()!=2) {
            //直接修改订单状态为退单成功
            info.setOrder_type(4);
            isok = orderinfoservice.update(info);
        }else {
            backOrder back = new backOrder();
            back.setOrder_number(info.getId());
            back.setBack_number(UUID.randomUUID().toString());
            back.setVersion(0);
            back.setBackstatus(1);
            back.setCreatetime(LocalDateTime.now());
            back.setUpdatetime(LocalDateTime.now());
            back.setOrder_pay_number(pay.getOrder_pay_number());
            back.setAmount(info.getAmount());
            back.setBackstatus(1);
            back.setUser_id(Integer.parseInt(CookieUtils.getLoginInfo(request)[1]));
            producers.send("back_order",back);
            isok=1;
        }
        model.addAttribute("isok",isok);
        return "backOrderPage";
    }

}
