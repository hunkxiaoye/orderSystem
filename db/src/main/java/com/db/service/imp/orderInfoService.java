package com.db.service.imp;

import com.db.dao.orderInfoDao;
import com.db.model.orderInfo;
import com.db.service.inf.iorderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class orderInfoService implements iorderInfoService {
    @Autowired
    private orderInfoDao dao;

    public int add(orderInfo model) {
        return dao.add(model);
    }

    public int update(orderInfo model) {
        return dao.update(model);

    }

    public List<orderInfo> findByStatus(int status) {
        return dao.findByStatus(status);
    }

    public List<orderInfo> findByuserid(int userid) {
        return dao.findByuserid(userid);
    }

    public orderInfo findByorderid(String orderid) {
        return dao.findByorderid(orderid);
    }
}
