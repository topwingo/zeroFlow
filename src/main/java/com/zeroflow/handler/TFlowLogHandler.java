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
    protected void saveExceptionLog(ErrorLog log) {
        elog.info(LogEvent.of("TflowInvoker-saveExceptionLog", "保存数据")
                .others("log", log)
        );

    }

    @Override
    protected void updateExceptionLog(ErrorLog log) {
        elog.info(LogEvent.of("TflowInvoker-updateExceptionLog", "修改数据")
                .others("log", log)
        );

    }

    @Override
    public List<ErrorLog> getErrorLogList() {
        ErrorLog log = new ErrorLog();
        String context = "{\"flowName\":\"TTTTTTTFlow\",\"t2Result\":\"i am retry\",\"t3Result\":[],\"uniqueCode\":\"uuid:11111111111111\",\"userID\":123456}";
        String commandRecord = "[\"T2\"]";
        log.setContext(context);
        log.setCommand_record(commandRecord);
        ArrayList<ErrorLog> list = new ArrayList<ErrorLog>();
        list.add(log);
        return list;
    }

}
