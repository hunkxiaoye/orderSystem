package com.db.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
//支付明细
public class orderPay {
    private int id;
    //主单号
    private String orderNumber;
    //支付单号
    private String orderPayNumber;
    //用户id
    private int userId;
    //支付流水号
    private String paySerialNumber;
    //支付状态
    private int payState;
    //支付信息
    private String payMsg;
    //支付金额
    private int payAmount;
    //支付字串
    private String payStr;
    //创建时间
    private LocalDateTime createTime;
    //支付成功时间
    private LocalDateTime paySucTime;
    //修改时间
    private LocalDateTime updateTime;
}
