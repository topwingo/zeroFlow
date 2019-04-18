package com.zeroflow.handler;

import com.zeroflow.base.BaseFlowHandler;
import com.zeroflow.base.BaseFlowLogHandler;
import com.zeroflow.bean.ErrorLog;
import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:测试日志管理器
 * @date:2019/4/11
 */
@Slf4j

public class TFlowLogHandler extends BaseFlowLogHandler {
    private EnhanceLogger elog = EnhanceLogger.of(log);

    @Override
    public void saveExceptionLog(ErrorLog log) {
        elog.info(LogEvent.of("TflowInvoker-saveExceptionLog", "保存错误日志数据")
                .others("log", log)
        );

    }

    @Override
    public void updateExceptionLog(ErrorLog log) {
        elog.info(LogEvent.of("TflowInvoker-updateExceptionLog", "更新错误日志数据")
                .others("log", log)
        );

    }

    @Override
    public List<ErrorLog> getErrorLogList() {
        elog.info(LogEvent.of("TflowInvoker-getErrorLogList", "读取重试列表数据")
        );

        ErrorLog log = new ErrorLog();
        String context = "{\"flowName\":\"com.zeroflow.handler.T2FlowHandle\",\"t2Result\":\"i am retry\",\"t3Result\":[],\"uniqueCode\":\"uuid:123456789\",\"userID\":00000001}";
        String commandRecord = "[\"T2\"]";
        log.setFlowName("com.zeroflow.handler.T2FlowHandle");
        log.setContext(context);
        log.setCommand_record(commandRecord);
        ArrayList<ErrorLog> list = new ArrayList<ErrorLog>();
        list.add(log);
        return list;
    }

}
