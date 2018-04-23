package com.job;

import com.common.kafka.AbstractConsumer;
import com.common.kafka.annotation.KafkaConf;
import com.common.util.FastJsonUtil;
import com.common.util.MD5;
import com.db.model.backOrder;
import com.db.model.refundModel;
import com.db.model.restfulModel;
import com.db.service.imp.backOrderService;
import com.db.service.imp.restfulService;
import org.apache.commons.codec.digest.Md5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Component
@KafkaConf(topic = "refund_order", groupid = "test_yp", threads = 1)
public class refundConsumer extends AbstractConsumer<refundModel> {
    protected static final Logger log = LoggerFactory.getLogger(refundConsumer.class);
    @Autowired
    restfulService service;
    @Autowired
    backOrderService backservice;

    protected boolean process(refundModel msg) {
        restfulModel rest;
        backOrder back;
        msg.setCreate_time(LocalDateTime.now());
        try {
            String msgs = FastJsonUtil.bean2Json(msg);
            String token = MD5.encrypt32(msgs + msg.getCreate_time());
            String uri = "";

            rest = service.initiatePay(msgs, token, uri);

            back = backservice.findByBackNumber(rest.getBack_number());
            if (rest.getStatus_code() == 200 || rest.getStatus_code() == 300) {
                back.setBackstatus(3);
                backservice.update(back);
            } else {
                back.setBackstatus(2);
                backservice.update(back);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return true;
    }
}

