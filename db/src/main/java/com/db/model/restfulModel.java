package com.db.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class restfulModel {
    //200成功 500失败 400超时 300重复退款
    private int status_code;
    //支付时间
    private LocalDateTime pay_time;
    //订单号
    private String orderid;
    //支付流水号
    private String pay_serial_number;
    //创建时间
    private LocalDateTime create_time;
    //退单流水号
    private String back_number;
}
