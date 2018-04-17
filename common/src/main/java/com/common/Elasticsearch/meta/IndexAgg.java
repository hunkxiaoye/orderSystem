package com.common.Elasticsearch.meta;
import com.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class IndexAgg {
    /**
     * 聚合字段
     * 不包含范围聚合字段
     */
    private Set<String> aggregation;

    /**
     * 对某个字段 执行聚合函数
     * 包括 ：求和  求最大值   求最小值  求平均值  求数量
     */
    private Map<String,Func[]> groupAgg;

    public IndexAgg() {
        this.aggregation = new HashSet<>();
        this.groupAgg = new HashMap<>();
    }


    public void addAggField(String fieldName){
        if (StringUtils.isEmpty(fieldName)){
            throw new RuntimeException("需要聚合的字段不可以为空");
        }
        this.aggregation.add(fieldName);
    }



    /**
     * 聚合函数枚举
     */
    public enum Func{

        /**
         * 求最小值
         */
        MIN("min"),

        /**
         * 求最大值
         */
        MAX("max"),

        /**
         * 求平均值
         */
        AVG("avg"),

        /**
         * 求和
         */
        SUM("sum"),

        /**
         * 求数量
         */
        COUNT("count");

        private String name;

        Func(String name) {
            this.name = name;
        }
    }


}
