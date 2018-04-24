package com.db.service.imp;

import com.db.dao.backOrderDao;
import com.db.model.backOrder;
import com.db.service.inf.ibackOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class backOrderService implements ibackOrderService {
    @Autowired
    private backOrderDao dao;

   public int add(backOrder model) {
       return dao.add(model);
   }
   public backOrder findBybackNumber(String id){
       return dao.findBybackNumber(id);
   }
   public List<backOrder> findByStatus(int status){
       return dao.findByStatus(status);
   }
   public int update(backOrder model){
       return dao.update(model);
   }
}
