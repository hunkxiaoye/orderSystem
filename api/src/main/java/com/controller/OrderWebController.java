package com.controller;


import com.db.model.orderInfo;
import com.db.model.orderPay;
import com.db.service.inf.iorderInfoService;
import com.db.service.inf.iorderPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
@RequestMapping("orderweb")
public class OrderWebController {

    @Autowired
    private iorderPayService orderpayservice;
    @Autowired
    private iorderInfoService orderinfoservice;

    /**
     * 直接返回字符串
     *
     * @param request
     * @return
     */
    //请求的路径，方式
    @RequestMapping(value = "orderPay", method = RequestMethod.GET)
    @ResponseBody
    public String orderPay(HttpServletRequest request) {
        if (request.getParameter("orderid") == null) {
            return "false";
        }
        LocalDateTime dateTime = LocalDateTime.now();
        orderInfo info = orderinfoservice.findByorderid(request.getParameter("orderid"));
        orderPay pay = orderpayservice.findbyorderid(request.getParameter("orderid"));
        switch (Integer.parseInt(request.getParameter("statusCode"))) {
            case 0:
                info.setOrder_type(2);
                pay.setPay_state(2);
                break;
            case 1:
                if (info.getOrder_type() != 2) {
                    info.setOrder_type(3);
                }
                if (pay.getPay_state() != 2) {
                    pay.setPay_state(3);
                }
                break;

        }
        pay.setPay_serial_number(request.getParameter("pay_serial_number"));
        info.setUpdate_time(dateTime);
        pay.setUpdate_time(dateTime);
        int a = orderinfoservice.update(info);
        int b = orderpayservice.update(pay);
        if (a == 1 && b == 1) {
            return "ture";
        } else {
            return "false";
        }

    }


}
