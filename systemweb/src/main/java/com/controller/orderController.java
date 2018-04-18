package com.controller;

import com.common.CookieUtils;
import com.common.cache.JedisUtil;
import com.common.constantCode;
import com.common.kafka.KafkaProducers;
import com.common.util.FastJsonUtil;
import com.common.util.MD5;
import com.common.util.Utils;
import com.db.model.*;
import com.db.service.inf.igoodsService;
import com.db.service.inf.iorderInfoService;
import com.db.service.inf.iorderPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class orderController {
    @Autowired
    private igoodsService service;
    @Autowired
    private iorderPayService orderpayservice;
    @Autowired
    private iorderInfoService orderinfoservice;
    @Autowired
    private KafkaProducers producers;

    /**
     * 生成订单
     * @param goodsid
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/ConfirmOrder")
    public String ConfirmOrder(Integer goodsid, Model model, HttpServletRequest request) throws Exception {

        String orderid = UUID.randomUUID().toString();
        Goods goods = service.findbyid(goodsid);

        //此demo每次只有一个商品
        if (goods.getStock() < 1) {
            model.addAttribute("isStock", 0);

        } else {
            stockModel stockModel = new stockModel();
            //此demo每次只有一个商品
            stockModel.setStock(1);
            stockModel.setId(goods.getId());
            int isok = service.update(stockModel);
            if (isok == 1) {
                //创建订单对象
                orderInfo orderInfo = new orderInfo();
                orderInfo.setId(orderid);
                orderInfo.setAmount(goods.getPrice());
                orderInfo.setCreate_time(LocalDateTime.now());
                orderInfo.setOrder_expired_time(LocalDateTime.now().plusMinutes(15));
                orderInfo.setOrder_type(0);
                orderInfo.setPay_status(0);
                orderInfo.setUpdate_time(LocalDateTime.now());
                orderInfo.setUserid(Integer.parseInt(CookieUtils.getLoginInfo(request)[1]));
                producers.send("order_create", orderInfo);

                //创建订单详情对象
                orderDetail orderDetail = new orderDetail();
                orderDetail.setOrder_id(orderid);
                orderDetail.setAmount(1);
                orderDetail.setCreate_time(LocalDateTime.now());
                orderDetail.setGoods_id(goods.getId());
                orderDetail.setIs_operating(0);
                orderDetail.setUpdate_time(LocalDateTime.now());
                producers.send("order_detail_create", orderDetail);

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

    /**
     * 生成支付订单明细
     * @param order_id
     * @param amount
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orderPay")
    public String orderPay(String order_id, Integer amount, Model model, HttpServletRequest request) throws Exception {
        String app_id = constantCode.getApp_id();
        int userid = Integer.parseInt(CookieUtils.getLoginInfo(request)[1]);
        LocalDateTime dateTime = LocalDateTime.now();

        //创建支付系统的入参
        payStr str = new payStr();
        str.setApp_id(app_id);
        str.setOrder_number(order_id);
        str.setPay_amount(amount);
        str.setUser_id(userid);
        str.setCreatetime(dateTime);
        str.setToken(MD5.encrypt32(app_id + order_id + dateTime));//生成的签名

        //创建订单支付明细
        orderPay order = new orderPay();
        order.setOrder_number(order_id);
        order.setPay_serial_number(UUID.randomUUID().toString());
        order.setUserid(userid);
        order.setPay_state(0);
        order.setPay_msg("");
        order.setPay_amount(amount);
        order.setCreate_time(dateTime);
        order.setPay_str(FastJsonUtil.bean2Json(str));
        producers.send("order_pay_create", order);

        model.addAttribute("orderid", order);
        model.addAttribute("amount", amount);
        return "confirmPayment";
    }

    /**
     * 支付
     * @param orderid
     * @param model
     * @return
     */
    @RequestMapping(value = "/payJump")
    public String payJump(String orderid, Model model) throws NoSuchAlgorithmException {
        String app_id = constantCode.getApp_id();
        LocalDateTime dateTime = LocalDateTime.now();
       orderPay pay = orderpayservice.findbyorderid(orderid);
       if (pay!=null){
           payStr str =FastJsonUtil.json2Bean(pay.getPay_str(),payStr.class);
           str.setCreatetime(dateTime);
           str.setToken(MD5.encrypt32(app_id + orderid +dateTime));//生成的签名
       }
      //向支付系统发起请求

      model.addAttribute("orderid",orderid);
      return "confirm";
    }

    /**
     * 支付状态确认
     * @param orderid
     * @param model
     * @return
     */
    @RequestMapping(value = "/orderJump")
    public String orderJump(String orderid,Model model){
       int isPaySuccess =0;
        orderPay models = orderpayservice.findbyorderid(orderid);
        payStr str =FastJsonUtil.json2Bean(models.getPay_str(),payStr.class);
        //通过订单号+签名获取响应参数

        //str.getToken()+orderid
        model.addAttribute("isPaySuccess",isPaySuccess);
        return "responsePage";
    }
    @RequestMapping(value = "/orderlist")
    public String orderlist(Model model,HttpServletRequest request) throws Exception {
        int userid = Integer.parseInt(CookieUtils.getLoginInfo(request)[1]);
        List<orderInfo> list = orderinfoservice.findByuserid(userid);
        model.addAttribute("list",list);
        return "orderList";
    }
}
