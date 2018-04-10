package com.common.Elasticsearch.meta;

public class ElasticIndex {
    String IndexName;

    String IndexType;

    public String getIndexName() {
        return IndexName;
    }

    public void setIndexName(String indexName) {
        IndexName = indexName;
    }

    public String getIndexType() {
        return IndexType;
    }

    public void setIndexType(String indexType) {
        IndexType = indexType;
    }
}
