package com.common.Elasticsearch.enums;

/**
 * Created by Mtime on 2016/10/18.
 */
public enum ESDateFormat {
    LocalDateTime("yyyy-MM-ddTHH:mm:ss"),
    LocalDate("yyyy-MM-dd");

    private String pattern;

    private ESDateFormat(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
