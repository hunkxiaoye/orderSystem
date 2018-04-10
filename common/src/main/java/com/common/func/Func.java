package com.common.func;

import java.io.IOException;

/**
 * @author
 * @create 2017-11-07 11:39.
 */
@FunctionalInterface
public interface Func<T> {

    void accept(T t) throws IOException;

}
