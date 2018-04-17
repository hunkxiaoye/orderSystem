package com.db.dao;

import com.db.model.orderInfo;

import java.util.List;

public interface orderInfoDao {
  int add(orderInfo model);
  int update(orderInfo model);
  List<orderInfo> findByStatus(int status);

}
