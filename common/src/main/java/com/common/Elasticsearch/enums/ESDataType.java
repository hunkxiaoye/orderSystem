package com.common.Elasticsearch.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mtime on 2016/10/18.
 */
public enum ESDataType {

    STRING("java.lang.String","keyword"),
    INTEGER("java.lang.Integer","integer"),
    INT("int","integer"),
    SHORT("java.lang.Short","short"),
    SSHORT("short","short"),
    BOOLEAN("java.lang.Boolean","boolean"),
    SBOOLEAN("boolean","boolean"),
    DOUBLE("java.lang.Double","double"),
    SDOUBLE("double","double"),
    FLOAT("java.lang.Float","float"),
    SFLOAT("float","float"),
    LOCALDATE("java.time.LocalDate","date"),
    LOCALDATETIME("java.time.LocalDateTime","date"),
    SLONG("long","long"),
    LONG("java.lang.Long","long"),
    OBJECT("java.lang.Object","object"),
    NESTED("Nested","nested");

    private String javaType;

    private String esType;

    private final static Map<String, String> caches = new ConcurrentHashMap<>();
    private static Object _lock = new Object();

    private ESDataType(String javaType, String esType) {
        this.javaType = javaType;
        this.esType = esType;
    }

    public String getEsType() {
        return esType;
    }

    public String getJavaType() {
        return javaType;
    }

    public static String getEsType(String javaType){
        if (caches.isEmpty()){
            synchronized (_lock){
                if (caches.isEmpty()){
                    for (ESDataType s : ESDataType.class.getEnumConstants()){
                        caches.put(s.getJavaType(),s.getEsType());
                    }
                }
            }
        }
        return caches.get(javaType);
    }





}
