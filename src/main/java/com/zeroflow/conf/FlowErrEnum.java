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
    PARTLY_SUCCCESS(201, "部分执行成功"),
    //通用异常
    ERROR(500, "内部执行异常"),
    SIGN_ERROR(501, "SIGN签名校验异常"),
    PARAMETER_ERROR(502, "参数错误异常"),
    INTERFACE_ERROR(503, "接口调用异常"),
    BIZ_ERROR(504, "业务执行异常"),
    DB_ERROR(505, "数据库执行异常"),
    DEGRADATION_ERROR(506, "业务降级异常"),
    ASYN_ERROR(507, "异步线程池异常"),
    REFLECT_INVOKE_ERROR(508, "反射执行异常");


    private  int code;
    private  String msg;

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
