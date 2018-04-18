package com.db.model;

import com.common.Elasticsearch.meta.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
//支付明细
public class orderPay extends BaseModel<Integer>{
   // private int id;
    //主单号
    private String order_number;
    //支付单号
    private String order_pay_number;
    //用户id
    private int userid;
    //支付流水号
    private String pay_serial_number;
    //支付状态
    private int pay_state;
    //支付信息
    private String pay_msg;
    //支付金额
    private int pay_amount;
    //支付字串
    private String pay_str;
    //创建时间
    private LocalDateTime create_time;
    //支付成功时间
    private LocalDateTime pay_suc_time;
    //修改时间
    private LocalDateTime update_time;
}
