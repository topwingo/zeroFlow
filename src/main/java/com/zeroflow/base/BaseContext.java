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
    private String uniqueCode="";
    private String flowName="";
}
