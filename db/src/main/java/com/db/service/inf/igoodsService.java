package com.db.service.inf;

import com.db.model.Goods;
import com.db.model.stockModel;

import java.util.List;

public interface igoodsService {
    List<Goods> findGoodsAll();
    Goods findbyid(Integer goodsid);
    int update(stockModel goods);
}
