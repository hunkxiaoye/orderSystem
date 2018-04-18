package com.db.service.inf;

import com.db.model.orderPay;

public interface iorderPayService {
    orderPay findbyorderid(String orderid);
    int add(orderPay model);
    int update(orderPay model);
}
