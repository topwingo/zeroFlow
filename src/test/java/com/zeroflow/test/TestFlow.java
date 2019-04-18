package com.zeroflow.test;

import com.zeroflow.base.BaseFlowHandler;
import com.zeroflow.bean.ErrorLog;
import com.zeroflow.bean.FlowResult;
import com.zeroflow.context.MyData;
import com.zeroflow.handler.T2FlowHandle;
import com.zeroflow.handler.TFlowHandle;
import com.zeroflow.handler.TFlowLogHandler;
import com.zeroflow.invoke.RetryInvoke;
import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:
 * @date:2019/4/8
 */
@Slf4j
public class TestFlow {
    private EnhanceLogger elog = EnhanceLogger.of(log);

    @Test
    public void execFlowT2() throws Exception {
        elog.info(LogEvent.of("TestFlow-execFlowT2", "执行流程"));
        //定义上下文数据，构建流程入参
        MyData data = new MyData();
        data.setUniqueCode("uuid:123456789");
        data.setUserID(123456);
        for (int i = 0; i < 1; i++) {
            //创建流程
            BaseFlowHandler handle = new T2FlowHandle();
            //设置上下文对象，设置日志管理器
            handle.setContext(data).setFlowLogHandler(TFlowLogHandler.class);
            //执行线程
            FlowResult<MyData> result = handle.invoke();
            elog.info(LogEvent.of("TestFlow-execFlowT2", "流程执行结果")
                    .analyze("result", result)
            );
        }
        Thread.sleep(1000);
    }

    @Test
    public void execFlow() throws Exception {
        elog.info(LogEvent.of("TestFlow-execFlow", "执行流程"));
        //定义上下文数据，构建流程入参
        MyData data = new MyData();
        data.setUniqueCode("uuid:123456789");
        data.setFlowName("TestFlow");
        data.setUserID(123456);
        for (int i = 0; i < 1; i++) {
            //创建流程
            BaseFlowHandler handle = new TFlowHandle();
            //设置上下文对象，设置日志管理器
            handle.setContext(data).setFlowLogHandler(TFlowLogHandler.class);
            //执行线程
            FlowResult<MyData> result = handle.invoke();
            elog.info(LogEvent.of("TestFlow-execFlow", "流程执行结果")
                    .analyze("result", result)
            );
        }
        Thread.sleep(1000);
    }

    @Test
    public void retry() throws Exception {
        elog.info(LogEvent.of("TestFlow-retry", "执行重试流程"));
        for (int i = 0; i < 1; i++) {
            //设置需要重试的流程，流程需要用到的日志管理器
            RetryInvoke invoke = new RetryInvoke(TFlowHandle.class, TFlowLogHandler.class);
            //执行重试
            invoke.invoke();
        }
        Thread.sleep(1000);
    }

    @Test
    public void singleRetry() throws Exception {
        elog.info(LogEvent.of("TestFlow-singleRetry", "重试单条记录"));
        ErrorLog log = new ErrorLog();
        String context = "{\"flowName\":\"singleretry-Flow\",\"t2Result\":\"i am singleRetry\",\"t3Result\":[],\"uniqueCode\":\"uuid:11111\",\"userID\":00000002}";
        String commandRecord = "[\"T2\"]";
        log.setContext(context);
        log.setCommand_record(commandRecord);

        //设置需要重试的流程，流程需要用到的日志管理器
        RetryInvoke invoke = new RetryInvoke(T2FlowHandle.class, TFlowLogHandler.class);
        //执行重试
        invoke.invoke(log);
    }
}

