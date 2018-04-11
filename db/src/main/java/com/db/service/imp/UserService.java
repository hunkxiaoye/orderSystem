package com.db.service.imp;

import com.db.dao.UserDao;
import com.db.model.User;
import com.db.service.inf.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserDao userdao;

    public User checklogin(String username, String pwd) {
        User user = userdao.findByUsername(username);
        if (user != null && user.getPwd().equals(pwd)) {
            return user;
        }
        return null;
    }
}
