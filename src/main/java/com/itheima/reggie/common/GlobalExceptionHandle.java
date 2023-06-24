package com.itheima.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 *   全局异常处理 捕获器
 */

// @ControllerAdvice 拦截   annotations  注解
//    拦截  标有该注解 的类
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody   //  返回 JSON数据
@Slf4j
public class GlobalExceptionHandle {

//    处理 SQLIntegrityConstraintViolationException 异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());  //  异常信息
        //  进一步 详细提示
        if(ex.getMessage().contains("Duplicate entry")){
//           根据 空格  将其 放进一个数组
            String[] splitList = ex.getMessage().split(" ");
//            split[2]  splitList数组第3个元素
            String msg ="账号："+splitList[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
    //    处理 SQLIntegrityConstraintViolationException 异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());  //  异常信息
//        获取打印的信息
        return R.error(ex.getMessage());
    }
}
