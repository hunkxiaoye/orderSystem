package com.common.kafka;

import com.common.kafka.annotation.KafkaConf;
import org.springframework.stereotype.Component;

@Component
@KafkaConf(topic = "yp_comment", groupid = "test_yp")
public class TestConsumer extends AbstractConsumer<String> {

    public boolean   process(String msg) {

        System.out.println("msg:" + msg);

        return false;
    }

}
