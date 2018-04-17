package com.db.service.imp;

import com.db.dao.orderDetailDao;
import com.db.model.orderDetail;
import com.db.service.inf.iorderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class orderDetailService implements iorderDetailService {
    @Autowired
    private orderDetailDao dao;

    public int add(orderDetail orderDetail) {
        return dao.add(orderDetail);
    }

    public int update(orderDetail orderDetail) {

        return dao.update(orderDetail);
    }
    public List<orderDetail> findbyid(String orderid){
        return dao.findbyid(orderid);
    }
}
