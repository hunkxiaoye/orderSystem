package com.db.model;

import com.common.Elasticsearch.meta.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class refundModel extends BaseModel<Integer> {
    //订单号
    private String order_number;
    //退单流水号
    private String back_number;
    //支付单号
    private String order_pay_number;
    //退款金额
    private int amount;
    //传参时间
    private LocalDateTime create_time;



}
