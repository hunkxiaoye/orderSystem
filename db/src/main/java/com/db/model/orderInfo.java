package com.db.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.Elasticsearch.meta.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class orderInfo extends BaseModel<String> {
    //订单号
    //private String id;
    //订单状态
    private int order_type;
    //支付状态
    private int pay_status;
    //订单金额
    private int amount;
    //创建时间
    private LocalDateTime create_time;
    //支付时间
    private LocalDateTime pay_time;
    //订单过期时间
    private LocalDateTime order_expired_time;
    //用户id
    private int userid;

}
