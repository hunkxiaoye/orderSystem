package com.common.Elasticsearch.meta;

import java.util.Map;

public class MappingProperties {

    /**
     * mapping properties
     * 结构为：
     * {
     *   field: {
     *     type:string,
     *     index:analyzed,
     *     store:true
     *   }
     * }
     */
    Map<String,Object> properties;

    /**
     * 是否动态映射字段类型
     */
    String dynamic ="strict";


    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public MappingProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
