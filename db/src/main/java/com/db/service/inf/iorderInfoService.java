package com.db.service.inf;

import com.db.model.orderInfo;

import java.util.List;

public interface iorderInfoService {
    int add(orderInfo model);

    int update(orderInfo model);

    List<orderInfo> findByStatus(int status);

    List<orderInfo> findByuserid(int userid);

    orderInfo findByorderid(String orderid);
}
