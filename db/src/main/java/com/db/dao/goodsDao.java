package com.db.dao;

import com.db.model.Goods;
import com.db.model.stockModel;

import java.util.List;

public interface goodsDao {
    List<Goods> findGoodsAll();
    Goods findbyid(Integer goodsid);
    int update(stockModel goods);
}
