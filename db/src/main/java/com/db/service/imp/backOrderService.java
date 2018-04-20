package com.db.service.imp;

import com.db.dao.backOrderDao;
import com.db.model.backOrder;
import com.db.service.inf.ibackOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class backOrderService implements ibackOrderService {
    @Autowired
    private backOrderDao dao;

   public int add(backOrder model) {
       return dao.add(model);
   }
}
