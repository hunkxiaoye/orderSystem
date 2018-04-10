package com.common.func;

import java.io.IOException;

/**
 * 有返回值的函数
 *
 * @author
 * @create 2017-11-15 15:37.
 */
@FunctionalInterface
public interface RFunc<T,R> {

    R accept(T t) throws IOException;

}
