package com.zeroflow.service;

import com.alibaba.fastjson.JSON;
import com.zeroflow.base.BaseFlowHandler;
import com.zeroflow.bean.FlowResult;
import com.zeroflow.context.MyData;
import com.zeroflow.handler.TFlowHandle;
import com.zeroflow.handler.TFlowLogHandler;
import com.zeroflow.invoke.RetryInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:
 * @date:2019/4/8
 */
public class FlowService {

    private final static Logger logger = LoggerFactory.getLogger(FlowService.class);

    private final static Logger zrender = LoggerFactory.getLogger("zrender");

    public FlowResult execFlow() throws Exception {

        //定义上下文数据，构建流程入参
        MyData data = new MyData();
        data.setUniqueCode("uuid:11111111111111");
        data.setFlowName("TTTTTTTFlow");
        data.setUserID(123456);
        //读取业务数据逻辑
        //执行流程，使用TFlowLogHandler，异常快照写入数据库
        BaseFlowHandler handle = new TFlowHandle();
        handle.setContext(data).setFlowLogHandler(TFlowLogHandler.class);
        return handle.invoke();
    }

    public void retry() throws Exception {
        //读取业务数据逻辑
        //定义重试那个流程快照数据，本例重试TFlowHandle的流程
        System.out.println("开启重试流程");
        RetryInvoke invoke = new RetryInvoke(TFlowHandle.class, TFlowLogHandler.class);
        invoke.invoke();
    }

    public static void main(String args[]) throws Exception {
        FlowService f = new FlowService();
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            System.out.println("流程结果:" + JSON.toJSONString(f.execFlow()));
            long end = System.currentTimeMillis() - start;
            System.out.println("执行时间：" + end);
        }
       // f.retry();
    }
}
