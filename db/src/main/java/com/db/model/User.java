package com.db.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//用户
public class User {
    private int id;
    private String userName;
    private String pwd;
    private int isAdmin;

}
