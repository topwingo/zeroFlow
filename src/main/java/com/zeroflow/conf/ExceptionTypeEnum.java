package com.zeroflow.conf;

import lombok.AllArgsConstructor;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description: 重试状态类型
 * @date:2018/12/14
 */
@AllArgsConstructor
public enum ExceptionTypeEnum {
    //已完成
    FINISHED(0),
    //重试状态
    RETRY(1),
    //不可重试状态
    FATAL(2);
    private int value;

    public int vaule() {
        return value;
    }
}
