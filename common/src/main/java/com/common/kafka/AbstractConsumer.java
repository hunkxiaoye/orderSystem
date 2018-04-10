package com.common.kafka;

import com.alibaba.fastjson.JSON;
import com.common.kafka.annotation.KafkaConf;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public abstract class AbstractConsumer<T> {

    protected static final Logger log = LoggerFactory.getLogger(AbstractConsumer.class);

    protected Class<T> msgClass;
    protected KafkaConf kafkaConf;
    //protected KafkaConsumer<String, String> kafkaConsumer = null;

    @Autowired
    private KafkaProperties kafkaProperties;

    public AbstractConsumer() {
        ResolvableType resolvableType = ResolvableType.forClass(this.getClass());
        this.msgClass = (Class<T>) (resolvableType.getSuperType().getGeneric(0).resolve());
        this.kafkaConf = this.getClass().getAnnotation(KafkaConf.class);
    }

    /**
     * 开始消费
     */
    public void start() {

        for (int i = 0; i < kafkaConf.threads(); i++) {
            Thread th = new Thread(() -> {
                KafkaConsumer<String, String> kafkaConsumer = init();
                try {
                    //获取消息
                    List<String> list = new ArrayList<>();
                    list.add(kafkaConf.topic());
                    kafkaConsumer.subscribe(list);
                    while (true) {
                        try {
                            ConsumerRecords<String, String> records = kafkaConsumer.poll(10000);
                            for (ConsumerRecord<String, String> record : records) {
                                try {
                                    T t = JSON.parseObject(record.value(), msgClass);
                                    this.process(t);
                                } catch (Exception e) {
                                    log.error("消费消息异常", e);
                                    e.printStackTrace();
                                }
                                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                            }
                        } catch (Exception e) {
                            log.error("获取消息异常", e);
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            th.start();
        }
    }

    private KafkaConsumer<String, String> init() {
        Properties properties = new Properties();
        properties.putAll(kafkaProperties.getConsumerPorpertis());
        properties.put("group.id", this.kafkaConf.groupid());
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        return kafkaConsumer;
    }


    protected abstract boolean process(T t);
}
