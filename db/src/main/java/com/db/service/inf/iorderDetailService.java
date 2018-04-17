package com.db.service.inf;

import com.db.model.orderDetail;

import java.util.List;

public interface iorderDetailService {
    int add(orderDetail orderDetail);
    int update(orderDetail orderDetail);
    List<orderDetail> findbyid(String orderid);
}
