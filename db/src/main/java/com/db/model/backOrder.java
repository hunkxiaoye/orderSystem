package com.db.model;

import com.common.Elasticsearch.meta.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class backOrder extends BaseModel<Integer>{
    //订单号
    private String order_number;
    //退单号
    private String back_number;
    //版本号
    private int version;
    //退单状态
    private int backstatus;
    //创建时间
    private LocalDateTime createtime;
    //更新时间
    private LocalDateTime updatetime;
    //支付单号
    private String order_pay_number;
    //退款金额
    private int amount;
    //退款状态
    private int back_pay_status;
    //退款成功时间
    private LocalDateTime refund_suc_time;
    //用户id
    private int user_id;

}
