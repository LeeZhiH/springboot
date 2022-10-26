package com.zhihao.zhiyin.common;

/**
 * @author 86159
 * @time 2022/9/18 17:53
 * 自定义异常类
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }
}
