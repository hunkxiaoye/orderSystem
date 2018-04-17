package com.common.Elasticsearch.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Mtime on 2016/10/17.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface SearchId {
}
