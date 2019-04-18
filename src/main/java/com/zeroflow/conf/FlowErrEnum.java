package com.zeroflow.conf;

import lombok.AllArgsConstructor;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description: 流程相关错误
 * @date:2018/12/14
 */
@AllArgsConstructor
public enum FlowErrEnum {
    //通用成功
    SUCCCESS(200, "执行成功"),
    //通用异常
    ERROR(500, "内部执行异常"),
    PRE_CHECK_ERROR(40001, "前置依赖异常"),
    UNIT_CONF_ERROR(40002, "异步线程池异常"),
    REFLECT_INVOKE_ERROR(40003, "反射执行异常");

    private  int code;
    private  String msg;

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
