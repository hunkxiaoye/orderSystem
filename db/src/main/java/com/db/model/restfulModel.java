package com.db.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class restfulModel {
    //200成功 500失败 400超时
    private int status_code;
    private LocalDateTime pay_time;
    private String orderid;
    private String pay_serial_number;
    private LocalDateTime create_time;
}
