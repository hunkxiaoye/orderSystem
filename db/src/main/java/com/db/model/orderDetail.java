package com.db.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;
@Setter
@Getter
//订单详情
public class orderDetail {
    private int id;
    //订单号
    private int orderId;
    //商品id
    private int goodsId;
    //数量
    private int amount;
    //是否回库
    private int isOperating;
    //创建时间
    private LocalDateTime createTime;
    //修改时间
    private LocalDateTime updateTime;

}
