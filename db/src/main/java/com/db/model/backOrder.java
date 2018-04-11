package com.db.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;
@Getter
@Setter
public class backOrder {
    //订单号
    private String orderNumber;
    //退单号
    private String backNumber;
    //版本号
    private int version;
    //退单状态
    private int backStatus;
    //创建时间
    private LocalDateTime createTime;
    //更新时间
    private LocalDateTime updateTime;
    //支付单号
    private String orderPayNumber;
    //退款金额
    private int amount;
    //退款状态
    private int backPayStatus;
    //退款成功时间
    private LocalDateTime refundSucTime;
    //用户id
    private int userId;

}
