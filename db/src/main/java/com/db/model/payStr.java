package com.db.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class payStr {
    private String order_number;
    private int pay_amount;
    private int user_id;
    private String app_id;
    private String token;
    private LocalDateTime createtime;
}
