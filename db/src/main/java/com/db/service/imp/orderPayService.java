package com.db.service.imp;

import com.db.dao.orderPayDao;
import com.db.model.orderPay;
import com.db.service.inf.iorderPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class orderPayService implements iorderPayService {
    @Autowired
    private orderPayDao dao;
   public orderPay findbyorderid(String orderid){
        return dao.findbyorderid(orderid);
    }
   public int add(orderPay model){
      return dao.add(model);
   }
   public int update(orderPay model){
       return dao.update(model);

   }
}
