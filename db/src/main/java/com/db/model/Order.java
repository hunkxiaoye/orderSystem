package com.db.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;
@Getter
@Setter
public class Order {
    //订单号
    private String orderNumber;
    //订单状态
    private int orderType;
    //支付状态
    private int payStatus;
    //订单金额
    private int amount;
    //创建时间
    private LocalDateTime createTime;
    //支付时间
    private LocalDateTime payTime;
    //订单过期时间
    private LocalDateTime orderExpiredTime;
    //用户id
    private int userId;

}
