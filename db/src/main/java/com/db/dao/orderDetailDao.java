package com.db.dao;

import com.db.model.orderDetail;

import java.util.List;

public interface orderDetailDao {
   int add(orderDetail orderDetail);
   int update(orderDetail orderDetail);
   List<orderDetail> findbyid(String orderid);
}
