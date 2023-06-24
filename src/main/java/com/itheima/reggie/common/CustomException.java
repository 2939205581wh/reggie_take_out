package com.itheima.reggie.common;

/**
 *   自定义业务异常类
 */
public class CustomException extends RuntimeException{
    /**
     *   打印提示信息
     * @param message
     */
    public CustomException(String message){
        super(message);
    }
}
