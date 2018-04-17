package com.db.service.inf;

import com.db.model.User;

public interface iuserService {
    User checklogin(String username, String pwd);
}
