package com.db.service.imp;

import com.db.dao.goodsDao;
import com.db.model.Goods;
import com.db.service.inf.igoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class goodsService implements igoodsService {
    @Autowired
    private goodsDao dao;

    public List<Goods> findGoodsAll() {
        return dao.findGoodsAll();
    }

    public Goods findbyid(Integer goodsid){
        return dao.findbyid(goodsid);
    }

    public int update(Goods goods){
        return dao.update(goods);
    }
}
