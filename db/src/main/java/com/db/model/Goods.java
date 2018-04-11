package com.db.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//商品信息
public class Goods {
    private int id;
    //商品名称
    private String  goodsName;
    //可用库存
    private int stock;
    //锁定库存
    private int lockStock;
    //价格
    private int price;

}
