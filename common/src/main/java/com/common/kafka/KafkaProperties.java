package com.common.kafka;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProperties {

    private Map<String, String> producerPorpertis = null;

    private Map<String, String> consumerPorpertis = null;
    //生产者配置
    @Value("${p.bootstrap.servers}")
    private String pbootstrapServers;
    @Value("${p.acks}")
    private String packs;
    @Value("${p.retries}")
    private String pretries;
    @Value("${p.batch.size}")
    private String pbatchSize;
    @Value("${p.auto.commit.interval.ms}")
    private String pintervalMs;
    @Value("${p.linger.ms}")
    private String plingerMs;
    @Value("${p.block.on.buffer.full}")
    private String pbuffer;


    //消费者配置
    @Value("${c.bootstrap.servers}")
    private String cbootstrapServers;
    @Value("${c.auto.commit.interval.ms}")
    private String cintervalMs;
    @Value("${c.enable.auto.commit}")
    private String cautoCommit;
    @Value("${c.session.timeout.ms}")
    private String csessionTimeoutms;
    @Value("${c.request.timeout.ms}")
    private String crequestTimeoutms;
    @Value("${c.max.poll.records}")
    private String crecords;
    @Value("${c.fetch.min.bytes}")
    private String cfetchMin;


    private void InitP() {
        if (producerPorpertis != null) return;
        synchronized (KafkaProperties.class) {
            if (producerPorpertis == null) {
                producerPorpertis = new HashMap<>();
                producerPorpertis.put("bootstrap.servers", pbootstrapServers);
                producerPorpertis.put("acks", packs);
                producerPorpertis.put("retries", pretries);
                producerPorpertis.put("batch.size", pbatchSize);
                producerPorpertis.put("auto.commit.interval.ms", pintervalMs);
                producerPorpertis.put("linger.ms", plingerMs);
                producerPorpertis.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                producerPorpertis.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                producerPorpertis.put("block.on.buffer.full", pbuffer);
            }
        }
    }

    private void InitC() {
        if (consumerPorpertis != null) return;
        synchronized (KafkaProperties.class) {
            if (consumerPorpertis == null) {
                consumerPorpertis = new HashMap<>();
                //props.put("zookeeper.connect", "hadoop2.jwl.com:2181,hadoop3.jwl.com:2181,hadoop4.jwl.com:2181");
                consumerPorpertis.put("bootstrap.servers", cbootstrapServers);
                //消费者的组id
                //consumerPorpertis.put("group.id", "test_yp");
                consumerPorpertis.put("enable.auto.commit", cautoCommit);
                /* 自动确认offset的时间间隔  */
                consumerPorpertis.put("auto.commit.interval.ms", cintervalMs);
                consumerPorpertis.put("session.timeout.ms", csessionTimeoutms);
                //消息发送的最长等待时间.需大于session.timeout.ms这个时间
                consumerPorpertis.put("request.timeout.ms", crequestTimeoutms);
                //一次从kafka中poll出来的数据条数
                //max.poll.records条数据需要在在session.timeout.ms这个时间内处理完
                consumerPorpertis.put("max.poll.records", crecords);
                //server发送到消费端的最小数据，若是不满足这个数值则会等待直到满足指定大小。默认为1表示立即接收。
                consumerPorpertis.put("fetch.min.bytes", cfetchMin);
                //若是不满足fetch.min.bytes时，等待消费端请求的最长等待时间
                consumerPorpertis.put("fetch.wait.max.ms", "1000");
                consumerPorpertis.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                consumerPorpertis.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            }
        }
    }

    public Map<String, String> getProducerPorpertis() {
        InitP();
        return producerPorpertis;
    }

    public Map<String, String> getConsumerPorpertis() {
        InitC();
        return consumerPorpertis;
    }
}

