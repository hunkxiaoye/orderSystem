package com.db.service.imp;

import com.common.cache.JedisUtil;
import com.db.dao.goodsDao;
import com.db.model.Goods;
import com.db.model.stockModel;
import com.db.service.inf.igoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class goodsService implements igoodsService {
    @Autowired
    private goodsDao dao;
    @Autowired
    private JedisUtil jedisUtil;

    public List<Goods> findGoodsAll() {
        return dao.findGoodsAll();
    }

    public Goods findbyid(Integer goodsid) {
        Goods goods = jedisUtil.get("goodsid" + goodsid, Goods.class);

        if (goods == null) {
            goods = dao.findbyid(goodsid);
            jedisUtil.set("goodsid" + goodsid, goods);

        }
        return goods;
    }

    public int update(stockModel goods) {
        int isok =dao.update(goods);
        if (isok==1){
            jedisUtil.set("goodsid" + goods.getId(),dao.findbyid(goods.getId()));
        }
        return isok;
    }
}
