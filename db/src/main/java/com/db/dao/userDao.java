package com.db.dao;

import com.db.model.User;

public interface userDao {
    User findByUsername(String username);
}
