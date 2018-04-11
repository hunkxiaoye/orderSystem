package com.db.service.inf;

import com.db.model.User;

public interface IUserService {
    User checklogin(String username, String pwd);
}
