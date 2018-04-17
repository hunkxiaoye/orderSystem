package com.common.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaProducers {

    private KafkaProducer<String, String> kafkaProducers = null;

    @Autowired
    private KafkaProperties kafkaProperties;

    private void init() {
        if (kafkaProducers == null) {
            synchronized (KafkaProducers.class) {
                if (kafkaProducers == null) {
                    Properties properties = new Properties();
                    properties.putAll(kafkaProperties.getProducerPorpertis());
                    kafkaProducers = new KafkaProducer<>(properties);
                }
            }
        }
    }

    /**
     * 生产消息
     *
     * @param topic 类型
     * @param msg   消息体
     */
    public void send(String topic, Object msg) {
        init();
        String key = String.valueOf(System.currentTimeMillis());
        kafkaProducers.send(new ProducerRecord<>(topic, key, JSON.toJSONString(msg)));
    }

    /**
     * 刷新
     */
    public void flush() {
        init();
        kafkaProducers.flush();
    }

}
