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
    //流程日志管理器
    private BaseFlowLogHandler flowLogHandler;

    /**
     * @param flowLogHandler 流程对应的日志管理器
     */
    public RetryInvoke(BaseFlowLogHandler flowLogHandler) {
        this.flowLogHandler = flowLogHandler;
    }

    /**
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
     * 自动根据日志的flowName字段分析出流程重试，flowName为流程className
     * @throws Exception
     */
    public void invoke() throws Exception {
        List<ErrorLog> errorLogList = flowLogHandler.getErrorLogList();
        elog.info(LogEvent.of("BaseRetryInvoke-invoke-Info", "执行批量重试")
                .others("错误日志条数", errorLogList.size())
        );
        for (ErrorLog errorLog : errorLogList) {
            try {
                Class flowClass = Class.forName(errorLog.getFlowName());
                BaseFlowHandler handler = (BaseFlowHandler) flowClass.newInstance();
                List<String> commandRecord = restoreCommandRecord(errorLog);
                handler.setContext(restoreContext(flowClass, errorLog)).setFlowLogHandler(flowLogHandler).setRetryParam(commandRecord, errorLog);
                handler.invoke();
            } catch (Exception ex) {
                elog.error(LogEvent.of("RetryInvoke-autoInvoke", "重试异常", ex)
                );
            }
        }
    }

    /**
     * 指定流程执行批量重试
     * @param flowHandlerClz 流程
     * @throws Exception
     */
    public void invoke(Class<? extends BaseFlowHandler> flowHandlerClz) throws Exception {
        List<ErrorLog> errorLogList = flowLogHandler.getErrorLogList();
        elog.info(LogEvent.of("BaseRetryInvoke-invoke", "执行批量重试")
                .others("错误日志条数", errorLogList.size())
        );
        for (ErrorLog errorLog : errorLogList) {
            invoke(flowHandlerClz, errorLog);
        }
    }

    /**
     * 指定流程执行单条记录重试
     * @param flowHandlerClz 流程
     * @param errorLog    错误日志
     * @throws Exception
     */
    public void invoke(Class<? extends BaseFlowHandler> flowHandlerClz, ErrorLog errorLog) throws Exception {
        elog.info(LogEvent.of("BaseRetryInvoke-invoke", "重试记录")
                .others("日志", errorLog)
        );
        List<String> commandRecord = restoreCommandRecord(errorLog);
        BaseFlowHandler handle = flowHandlerClz.newInstance();
        handle.setContext(restoreContext(flowHandlerClz, errorLog)).setFlowLogHandler(flowLogHandler).setRetryParam(commandRecord, errorLog);
        handle.invoke();
    }

    /**
     * 解释上下文为对应对象
     *
     * @param log
     * @return
     */
    protected <T> T restoreContext(Class flowHandler, ErrorLog log) {
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
