package com.db.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class responseModel {
    private LocalDateTime time;
    private int isok;
    private String  app_id;
    private String Pay_serial_number;
}
