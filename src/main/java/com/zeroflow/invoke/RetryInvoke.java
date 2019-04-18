package com.zeroflow.invoke;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zeroflow.base.BaseFlowHandler;
import com.zeroflow.base.BaseFlowLogHandler;
import com.zeroflow.bean.ErrorLog;
import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:基础重试调用器
 * @date:2019/4/11
 */
@Slf4j
public class RetryInvoke {
    private EnhanceLogger elog = EnhanceLogger.of(log);
    //流程执行器
    private Class<? extends BaseFlowHandler> flowHandler;
    //流程日志管理器
    private BaseFlowLogHandler flowLogHandler;

    /**
     * @param flowLogHandler 流程对应的日志管理器
     */
    public RetryInvoke(BaseFlowLogHandler flowLogHandler) {
        this.flowLogHandler = flowLogHandler;
    }

    /**
     *
     * @param flowLogHandlerClz 流程对应的日志管理器
     */
    public RetryInvoke(Class<? extends BaseFlowLogHandler> flowLogHandlerClz) {
        try {
            this.flowLogHandler = flowLogHandlerClz.newInstance();
        } catch (Exception ex) {
            elog.error(LogEvent.of("RetryInvoke-RetryInvoke", "初始化FlowLogHandler异常", ex)
            );
        }
    }

    /**
     * @param flowHandlerClz    流程对应的管理器
     * @param flowLogHandlerClz 流程对应的日志管理器
     */
    public RetryInvoke(Class<? extends BaseFlowHandler> flowHandlerClz, Class<? extends BaseFlowLogHandler> flowLogHandlerClz) {
        this.flowHandler = flowHandlerClz;
        try {
            this.flowLogHandler = flowLogHandlerClz.newInstance();
        } catch (Exception ex) {
            elog.error(LogEvent.of("RetryInvoke-RetryInvoke", "初始化FlowLogHandler异常", ex)
            );
        }
    }

    /**
     * @param flowHandlerClz 流程对应的管理器
     * @param flowLogHandler 流程对应的日志管理器
     */
    public RetryInvoke(Class<? extends BaseFlowHandler> flowHandlerClz, BaseFlowLogHandler flowLogHandler) {
        this.flowHandler = flowHandlerClz;
        this.flowLogHandler = flowLogHandler;
    }

    /**
     * 自动根据日志的flowName分析数据进行重试，flowName只能生成类名
     *
     * @throws Exception
     */
    public void autoInvoke() throws Exception {
        List<ErrorLog> errorLogList = flowLogHandler.getErrorLogList();
        elog.info(LogEvent.of("BaseRetryInvoke-invoke-Info", "执行批量重试")
                .others("错误日志条数", errorLogList.size())
        );
        for (ErrorLog errorLog : errorLogList) {
            System.out.println(JSON.toJSONString(errorLog));
            try {
                System.out.println("class:"+errorLog.getFlowName());
                Class flowClass =Class.forName(errorLog.getFlowName());
                BaseFlowHandler flowHandler = (BaseFlowHandler) flowClass.newInstance();
                List<String> commandRecord = restoreCommandRecord(errorLog);
                flowHandler.setContext(restoreContext(flowClass,errorLog)).setFlowLogHandler(flowLogHandler).setRetryParam(commandRecord, errorLog);
                flowHandler.invoke();
            } catch (Exception ex) {
                elog.error(LogEvent.of("RetryInvoke-autoInvoke", "重试异常", ex)
                );
            }
        }
    }

    /**
     * 执行重试
     *
     * @throws Exception
     */
    public void invoke() throws Exception {
        List<ErrorLog> errorLogList = flowLogHandler.getErrorLogList();
        elog.info(LogEvent.of("BaseRetryInvoke-invoke-Info", "执行批量重试")
                .others("错误日志条数", errorLogList.size())
        );
        for (ErrorLog errorLog : errorLogList) {
            List<String> commandRecord = restoreCommandRecord(errorLog);
            BaseFlowHandler handle = flowHandler.newInstance();
            handle.setContext(restoreContext(flowHandler,errorLog)).setFlowLogHandler(flowLogHandler).setRetryParam(commandRecord, errorLog);
            handle.invoke();
        }
    }

    /**
     * 执行重试单个命令
     *
     * @throws Exception
     */
    public void invoke(ErrorLog errorLog) throws Exception {
        elog.info(LogEvent.of("BaseRetryInvoke-invoke-Info", "重试流程记录")
                .others("日志", errorLog)
        );
        List<String> commandRecord = restoreCommandRecord(errorLog);
        BaseFlowHandler handle = flowHandler.newInstance();
        handle.setContext(restoreContext(flowHandler,errorLog)).setFlowLogHandler(flowLogHandler).setRetryParam(commandRecord, errorLog);
        handle.invoke();
    }

    /**
     * 解释上下文为对应对象
     *
     * @param log
     * @return
     */
    protected <T> T restoreContext(Class flowHandler,ErrorLog log) {
        Class<T> clazz = getSuperClassGenricType(flowHandler, 0);
        T context = JSON.parseObject(log.getContext(), clazz);
        return context;
    }

    /**
     * 解释命令列表为JAVA对象
     *
     * @param log
     * @return
     */
    protected List<String> restoreCommandRecord(ErrorLog log) {
        ArrayList<String> commandRecord = JSON.parseObject(log.getCommand_record(), new TypeReference<ArrayList<String>>() {
        });
        return commandRecord;
    }

    /**
     * 通过反射,获得定义Class时声明的最终父类的范型参数的类型
     *
     * @param clazz
     * @param index 返回某下标的类型
     */
    protected Class getSuperClassGenricType(Class clazz, int index) throws IndexOutOfBoundsException {
        Class finalSuperClass = clazz;
        //向上查找，直到BaseFlowHandler,用于查出对应的Context类型
        while (finalSuperClass.getSuperclass() != BaseFlowHandler.class) {
            finalSuperClass = finalSuperClass.getSuperclass();
        }
        Type genType = finalSuperClass.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

}
