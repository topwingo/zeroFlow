package com.zeroflow.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:获取流程属性
 * @date:2019/4/8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unit {
    //单元名称
    String name() default "";
    //执行顺序
    int order() default 0;
    //是否异步
    boolean asyn() default false;
    //是否启用此流程单元
    boolean enable() default true;
    //前置检查条件
    String[] preCheck() default {};
}
