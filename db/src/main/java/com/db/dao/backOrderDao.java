package com.db.dao;

import com.db.model.backOrder;

public interface backOrderDao {
    int add(backOrder model);
    backOrder findByBackNumber(String id);
   int update(backOrder model);
}
