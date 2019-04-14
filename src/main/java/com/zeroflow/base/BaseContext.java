package com.zeroflow.base;

import lombok.Data;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:基础上下文属性
 * @date:2019/4/9
 */
@Data
public class BaseContext {
    private long userID=0;
    //每条业务执行的唯一记录号
    private String uniqueCode="";
    //业务名称
    private String flowName="";
}
