package com.db.service.inf;

import com.db.model.backOrder;

public interface ibackOrderService {
    int add(backOrder model);
    backOrder findByBackNumber(String id);
    int update(backOrder model);
}
