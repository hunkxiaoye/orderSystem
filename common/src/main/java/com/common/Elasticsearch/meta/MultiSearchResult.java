package com.common.Elasticsearch.meta;

import java.util.List;

public class MultiSearchResult<T,U> {
    private long searchCount;

    private List<T> searchListT;

    private List<U> searchListU;

    public long getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(long searchCount) {
        this.searchCount = searchCount;
    }

    public List<T> getSearchListT() {
        return searchListT;
    }

    public void setSearchListT(List<T> searchListT) {
        this.searchListT = searchListT;
    }

    public List<U> getSearchListU() {
        return searchListU;
    }

    public void setSearchListU(List<U> searchListU) {
        this.searchListU = searchListU;
    }
}
