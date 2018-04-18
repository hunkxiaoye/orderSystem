package com.db.dao;

import com.db.model.orderPay;

public interface orderPayDao {
   orderPay findbyorderid(String orderid);
   int add(orderPay model);
   int update(orderPay model);
}
