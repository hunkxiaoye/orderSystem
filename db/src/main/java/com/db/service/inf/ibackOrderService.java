package com.db.service.inf;

import com.db.model.backOrder;

import java.util.List;

public interface ibackOrderService {
    int add(backOrder model);
    backOrder findBybackNumber(String id);
    List<backOrder> findByStatus(int status);
    int update(backOrder model);
}
