package com.db.dao;

import com.db.model.User;

public interface UserDao {
    User findByUsername(String username);
}
