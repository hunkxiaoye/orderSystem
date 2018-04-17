package com.common.Elasticsearch.enums;

/**
 * 是否索引
 *
 * @author
 * @create
 */
public enum Indexed {

    INDEXED("true"),
    NOT_INDEXED("false");

    private String name;


    private Indexed(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
