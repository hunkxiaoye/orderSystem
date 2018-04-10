package com.common.Elasticsearch.enums;

/**
 * Created by Mtime on 2016/10/17.
 */
public enum Analyzed {

    ANALYZED("analyzed"),
    NOT_ANALYZED("not_analyzed");

    private String name;


    private Analyzed(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
