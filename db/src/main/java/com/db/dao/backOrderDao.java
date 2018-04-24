package com.db.dao;

import com.db.model.backOrder;

import java.util.List;

public interface backOrderDao {
    int add(backOrder model);
    backOrder findBybackNumber(String id);
    List<backOrder> findByStatus(int status);
   int update(backOrder model);
}
