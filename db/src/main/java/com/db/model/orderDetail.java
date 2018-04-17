package com.db.model;

import com.common.Elasticsearch.meta.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
//订单详情
public class orderDetail extends BaseModel<Integer> {
   // private int id;
    //订单号
    private String order_id;
    //商品id
    private int goods_id;
    //数量
    private int amount;
    //是否回库
    private int is_operating;
    //创建时间
    private LocalDateTime create_time;
    //修改时间
    private LocalDateTime update_time;

}
