package com.db.service.inf;

import com.db.model.Goods;

import java.util.ArrayList;
import java.util.List;

public interface igoodsService {
    List<Goods> findGoodsAll();
    Goods findbyid(Integer goodsid);
    int update(Goods goods);
}
