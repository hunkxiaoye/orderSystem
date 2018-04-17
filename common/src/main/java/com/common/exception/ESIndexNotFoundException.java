package com.common.exception;

/**
 * ES索引没有找到的异常
 *
 * @author
 * @create 2017-11-07 10:55.
 */
public class ESIndexNotFoundException extends RuntimeException {

    public ESIndexNotFoundException(){
        super();
    }

    public ESIndexNotFoundException(String message){
        super(message);
    }

    public ESIndexNotFoundException(String message, Throwable cause){
        super(message,cause);
    }
    public ESIndexNotFoundException(Throwable cause){
        super(cause);
    }

}
