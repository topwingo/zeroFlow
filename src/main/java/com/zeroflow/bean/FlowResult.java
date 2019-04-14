package com.zeroflow.bean;

import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:流程结果
 * @date:2019/4/11
 */
@Data
public class FlowResult<T> {
    private int code=500;
    private String msg="内部错误";
    private T data;

    public FlowResult(){

    }

    public FlowResult(int code,String msg,T data){
        this.code=code;
        this.msg= ObjectUtils.defaultIfNull(msg, StringUtils.EMPTY);
        this.data=data;
    }

    public void of(int code,String msg){
        this.code=code;
        this.msg= ObjectUtils.defaultIfNull(msg, StringUtils.EMPTY);
    }
}
