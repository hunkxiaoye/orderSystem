package com.common.Elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


@Component
public class esClientUtil {

    private static volatile Map<String, RestHighLevelClient> rhClient = new HashMap<>();
    private static volatile Map<String, RestClient> restClients = new HashMap<>();
    private static final Object _lock = new Object();

    @Value("${eshost}")
    private String host;
    @Value("${esshards}")
    private String shards;
    @Value("${esreplicas}")
    private String replicas;
    @Value("${esmaxresult}")
    private String maxresult;

    public RestHighLevelClient getClient(String clusterName) {
        try {
            if (!rhClient.containsKey(clusterName)) {
                synchronized (_lock) {
                    if (!rhClient.containsKey(clusterName)) {
                        if (!restClients.containsKey(clusterName)) {
                            getrestClient(clusterName);
                        }
                        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClients.get(clusterName));
                        rhClient.put(clusterName, restHighLevelClient);
                    }
                }
            }

            return rhClient.get(clusterName);

        } catch (UnknownHostException e) {
            throw new RuntimeException("elasticearch 连接配置不正确", e);
        }

    }

    public RestClient getrestClient(String clusterName) throws UnknownHostException {
        if (!restClients.containsKey(clusterName)) {
            synchronized (_lock) {
                if (!restClients.containsKey(clusterName)) {
                    String[] hosts = host.split(",");
                    HttpHost[] httpHosts = new HttpHost[hosts.length];
                    for (int i = 0; i < hosts.length; i++) {
                        String[] add = hosts[i].split(":");
                        httpHosts[i] = new HttpHost(InetAddress.getByName(add[0]), Integer.parseInt(add[1]), "http");
                        RestClient restClient = RestClient.builder(httpHosts).setMaxRetryTimeoutMillis(5 * 60 * 1000).build();
                        restClients.put(clusterName, restClient);
                    }
                }
            }
        }
        return restClients.get(clusterName);

    }

    public String getshards() {
        return this.shards = shards;
    }

    public String getreplicas() {
        return this.replicas = replicas;
    }

    public String getmaxresult() {
        return this.maxresult = maxresult;
    }

}
