package com.common.exception;

/**
 * ES索引映射没有找到的异常
 *
 * @author
 * @create 2017-11-07 10:55.
 */
public class ESMappingNotFoundException extends RuntimeException {

    public ESMappingNotFoundException(){
        super();
    }

    public ESMappingNotFoundException(String message){
        super(message);
    }

    public ESMappingNotFoundException(String message, Throwable cause){
        super(message,cause);
    }
    public ESMappingNotFoundException(Throwable cause){
        super(cause);
    }

}
