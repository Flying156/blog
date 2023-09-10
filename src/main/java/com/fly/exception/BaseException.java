package com.fly.exception;


/**
 * 业务异常
 */
public class BaseException extends RuntimeException{
    public BaseException(String msg){
        super(msg);
    }
}
