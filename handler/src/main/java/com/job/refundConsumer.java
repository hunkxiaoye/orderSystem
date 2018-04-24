package com.job;

import com.common.kafka.AbstractConsumer;
import com.common.kafka.annotation.KafkaConf;
import com.common.util.FastJsonUtil;
import com.common.util.MD5;
import com.db.model.backOrder;
import com.db.model.orderInfo;
import com.db.model.refundModel;
import com.db.model.restfulModel;
import com.db.service.imp.backOrderService;
import com.db.service.imp.restfulService;
import com.db.service.inf.ibackOrderService;
import com.db.service.inf.iorderInfoService;
import com.db.service.inf.irestfulService;
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
    irestfulService service;
    @Autowired
    ibackOrderService backservice;
    @Autowired
    iorderInfoService infoService;

    protected boolean process(refundModel msg) {
        restfulModel rest =new restfulModel();
        backOrder back;
        msg.setCreate_time(LocalDateTime.now());
        try {
            String msgs = FastJsonUtil.bean2Json(msg);
            String token = MD5.encrypt32(msgs + msg.getCreate_time());
            String uri = "";

            //rest = service.initiatePay(msgs, token, uri);
             rest.setStatus_code(200);
             rest.setBack_number(msg.getBack_number());

                back = backservice.findBybackNumber(rest.getBack_number());
                orderInfo info = infoService.findByorderid(back.getOrder_number());

            if (rest.getStatus_code() == 200 || rest.getStatus_code() == 300) {
                back.setBackstatus(3);
                back.setBack_status(2);
                info.setOrder_type(4);
                infoService.update(info);
                backservice.update(back);
            } else {
                back.setBackstatus(2);
                back.setBack_status(3);
                info.setOrder_type(5);
                infoService.update(info);
                backservice.update(back);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }
}

