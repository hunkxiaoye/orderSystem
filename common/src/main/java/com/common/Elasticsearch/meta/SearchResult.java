package com.common.Elasticsearch.meta;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SearchResult<T> {
    private Integer searchCount;

    private List<T> searchList;

    private Map<String,Map<Object,Long>> aggResult;

    private Map<String,Map<IndexAgg.Func,Object>> groupResult;

    private String scrollId;

}
