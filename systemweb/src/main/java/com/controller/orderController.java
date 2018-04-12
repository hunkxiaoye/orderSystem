package com.controller;

import com.common.CookieUtils;
import com.common.cache.JedisUtil;
import com.common.kafka.KafkaProducers;
import com.common.util.FastJsonUtil;
import com.common.util.MD5;
import com.db.model.Goods;
import com.db.model.orderInfo;
import com.db.model.orderPay;
import com.db.model.payStr;
import com.db.service.inf.igoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class orderController {
    @Autowired
    private igoodsService service;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private KafkaProducers producers;
 private static final String app_id ="21837198273981273981273918273";
    @RequestMapping(value = "/ConfirmOrder")
    public String ConfirmOrder(Integer goodsid, Model model, HttpServletRequest request) throws Exception {
        String orderid = UUID.randomUUID().toString();
        Goods goods = jedisUtil.get("goodsid" + goodsid, Goods.class);
        if (goods == null) {
            jedisUtil.set("goodsid" + goodsid, service.findbyid(goodsid));
        }
        //此demo每次只有一个商品
        if (goods.getStock() < 1) {
            model.addAttribute("isStock", 0);
        } else {
            int isok = service.update(goods);
            if (isok == 1) {
                //创建订单对象
                orderInfo orderInfo = new orderInfo();
                orderInfo.setOrder_number(orderid);
                orderInfo.setAmount(goods.getPrice());
                orderInfo.setCreate_time(LocalDateTime.now());
                orderInfo.setOrder_expired_time(LocalDateTime.now().plusMinutes(15));
                orderInfo.setOrder_type(0);
                orderInfo.setPay_status(0);
                orderInfo.setUserid(Integer.parseInt(CookieUtils.getLoginInfo(request)[1]));
                producers.send("order_create", orderInfo);
                model.addAttribute("isStock", 1);
                model.addAttribute("goods", goods);
                model.addAttribute("order_id", orderid);
                model.addAttribute("amount", goods.getPrice());
            } else {
                model.addAttribute("isStock", 0);
            }

        }

        return "ConfirmOrderPage";

    }

    public String orderPay(String order_id,Integer amount,HttpServletRequest request) throws Exception {
      int userid = Integer.parseInt(CookieUtils.getLoginInfo(request)[1]);
        LocalDateTime dateTime =LocalDateTime.now();
        //创建支付系统的入参
        payStr str = new payStr();
        str.setApp_id(app_id);
        str.setOrder_number(order_id);
        str.setPay_amount(amount);
        str.setUser_id(userid);
        str.setCreatetime(dateTime);
        str.setToken(MD5.encrypt32(app_id+dateTime));//生成的签名
        //创建订单支付明细
        orderPay order = new orderPay();
        order.setOrderNumber(order_id);
        order.setPaySerialNumber(UUID.randomUUID().toString());
        order.setUserId(userid);
        order.setPayState(1);
        order.setPayMsg("");
        order.setPayAmount(amount);
        order.setCreateTime(dateTime);
        order.setPayStr(FastJsonUtil.bean2Json(str));
          return "";
    }
}
