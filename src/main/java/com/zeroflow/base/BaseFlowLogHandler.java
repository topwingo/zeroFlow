package com.zeroflow.base;

import com.alibaba.fastjson.JSON;
import com.zeroflow.bean.ErrorLog;
import com.zeroflow.bean.FlowResult;
import com.zeroflow.conf.ExceptionTypeEnum;
import com.zeroflow.conf.FlowErrEnum;
import com.zeroflow.exception.CriticalException;
import com.zeroflow.exception.DiscardException;
import com.zeroflow.exception.RetryException;
import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:基础执行器，用于保存异常数据到数据库
 * @date:2019/4/9
 */
@Slf4j
public abstract class BaseFlowLogHandler {
    private EnhanceLogger elog = EnhanceLogger.of(log);

    /**
     * 新增错误日志
     *
     * @param log
     */
    protected abstract void saveExceptionLog(ErrorLog log);

    /**
     * 修改增错误日志，以log的id判断，非0代表重试流程数据
     *
     * @param log
     */
    protected abstract void updateExceptionLog(ErrorLog log);


    /**
     * 读取错误日志列表
     *
     * @return
     */
    public abstract List<ErrorLog> getErrorLogList();


    public FlowResult invoke(BaseFlowHandler handler, ErrorLog log) {
        FlowResult result = new FlowResult();
        try {
            result = handler.execCommandList();
        } catch (RetryException ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "RetryException异常", ex)
                    .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                    .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                    .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                    .others("code", ex.getCode())
                    .others("msg", ex.getMessage())
                    .others("exceptionCommand", ex.getExceptionCommand())
                    .others("commandRecourd", ex.getCommandRecord())
                    .others("context", ex.getContext())
            );
            result.of(ex.getCode(), ex.getMessage());
            ErrorLog errorLog;
            if (null != log) {
                errorLog = log;
                errorLog.setRetry_num(errorLog.getRetry_num() + 1);
            } else {
                errorLog = new ErrorLog();
            }
            errorLog.of(ex.getCode(), ex.getMessage(), ((BaseContext) ex.getContext()).getUserID(),
                    ((BaseContext) ex.getContext()).getUniqueCode(), ((BaseContext) ex.getContext()).getFlowName(),
                    JSON.toJSONString(ex.getContext()), ex.getExceptionCommand(), JSON.toJSONString(ex.getCommandRecord()),
                    ExceptionTypeEnum.RETRY.vaule(), errorLog.getRetry_num());

            if (null != log) {
                updateExceptionLog(errorLog);

            } else {
                saveExceptionLog(errorLog);
            }
        } catch (CriticalException ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "CriticalException异常", ex)
                    .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                    .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                    .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                    .others("code", ex.getCode())
                    .others("msg", ex.getMessage())
                    .others("exceptionCommand", ex.getExceptionCommand())
                    .others("commandRecourd", ex.getCommandRecord())
                    .others("context", ex.getContext())
            );
            result.of(ex.getCode(), ex.getMessage());
            ErrorLog errorLog;
            if (null != log) {
                errorLog = log;
                errorLog.setRetry_num(errorLog.getRetry_num() + 1);
            } else {
                errorLog = new ErrorLog();
            }
            errorLog.of(ex.getCode(), ex.getMessage(), ((BaseContext) ex.getContext()).getUserID(),
                    ((BaseContext) ex.getContext()).getUniqueCode(), ((BaseContext) ex.getContext()).getFlowName(),
                    JSON.toJSONString(ex.getContext()), ex.getExceptionCommand(), JSON.toJSONString(ex.getCommandRecord()),
                    ExceptionTypeEnum.FATAL.vaule(), errorLog.getRetry_num());
            if (null != log) {
                updateExceptionLog(errorLog);

            } else {
                saveExceptionLog(errorLog);
            }
        } catch (DiscardException ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "DiscardException异常", ex)
                    .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                    .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                    .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                    .others("code", ex.getCode())
                    .others("msg", ex.getMessage())
                    .others("exceptionCommand", ex.getExceptionCommand())
                    .others("commandRecourd", ex.getCommandRecord())
                    .others("context", ex.getContext())
            );
            result.of(ex.getCode(), ex.getMessage());
            //兼容幂等请求，同一条记录重复请求会抛出SUCCCESS的SvipDiscardException异常.
            if (FlowErrEnum.SUCCCESS.code() == ex.getCode()) {
                result.setData(ex.getData());
                elog.info(LogEvent.of("FlowInvoker-invoke-INFO", "幂等请求")
                        .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                        .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                        .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                );
            }
            if (null != log) {
                log.setRetry_num(log.getRetry_num() + 1);
                log.of(ex.getCode(), ex.getMessage(), ((BaseContext) ex.getContext()).getUserID(),
                        ((BaseContext) ex.getContext()).getUniqueCode(), ((BaseContext) ex.getContext()).getFlowName(),
                        JSON.toJSONString(ex.getContext()), ex.getExceptionCommand(), JSON.toJSONString(ex.getCommandRecord()),
                        ExceptionTypeEnum.FATAL.vaule(), log.getRetry_num());
            }
        } catch (Exception ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "Exception异常", ex)
                    .others("msg", ex.getMessage())
            );
            result.of(FlowErrEnum.ERROR.code(), FlowErrEnum.ERROR.msg());
        }
        return result;
    }


    public void asynInvoke(BaseFlowHandler handler, String command, List commandRecord) {
        try {
            handler.asynInvoke(command, commandRecord);
        } catch (RetryException ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "RetryException异常", ex)
                    .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                    .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                    .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                    .others("code", ex.getCode())
                    .others("msg", ex.getMessage())
                    .others("exceptionCommand", ex.getExceptionCommand())
                    .others("commandRecourd", ex.getCommandRecord())
                    .others("context", ex.getContext())
            );
            ErrorLog errorLog = new ErrorLog();
            errorLog.of(ex.getCode(), ex.getMessage(), ((BaseContext) ex.getContext()).getUserID(),
                    ((BaseContext) ex.getContext()).getUniqueCode(), ((BaseContext) ex.getContext()).getFlowName(),
                    JSON.toJSONString(ex.getContext()), ex.getExceptionCommand(), JSON.toJSONString(ex.getCommandRecord()),
                    ExceptionTypeEnum.RETRY.vaule(), 0);
            saveExceptionLog(errorLog);
        } catch (CriticalException ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "CriticalException异常", ex)
                    .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                    .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                    .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                    .others("code", ex.getCode())
                    .others("msg", ex.getMessage())
                    .others("exceptionCommand", ex.getExceptionCommand())
                    .others("commandRecourd", ex.getCommandRecord())
                    .others("context", ex.getContext())
            );
            ErrorLog errorLog = new ErrorLog();
            errorLog.of(ex.getCode(), ex.getMessage(), ((BaseContext) ex.getContext()).getUserID(),
                    ((BaseContext) ex.getContext()).getUniqueCode(), ((BaseContext) ex.getContext()).getFlowName(),
                    JSON.toJSONString(ex.getContext()), ex.getExceptionCommand(), JSON.toJSONString(ex.getCommandRecord()),
                    ExceptionTypeEnum.FATAL.vaule(), 0);
            saveExceptionLog(errorLog);
        } catch (DiscardException ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "DiscardException异常", ex)
                    .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                    .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                    .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                    .others("code", ex.getCode())
                    .others("msg", ex.getMessage())
                    .others("exceptionCommand", ex.getExceptionCommand())
                    .others("commandRecourd", ex.getCommandRecord())
                    .others("context", ex.getContext())
            );
            //兼容幂等请求，同一条记录重复请求会抛出SUCCCESS的SvipDiscardException异常.
            if (FlowErrEnum.SUCCCESS.code() == ex.getCode()) {
                elog.info(LogEvent.of("FlowInvoker-invoke-INFO", "幂等请求")
                        .analyze("userId", ((BaseContext) ex.getContext()).getUserID())
                        .analyze("unique_code", ((BaseContext) ex.getContext()).getUniqueCode())
                        .analyze("flow_name", ((BaseContext) ex.getContext()).getFlowName())
                );
            }
        } catch (Exception ex) {
            elog.error(LogEvent.of("FlowInvoker-invoke-ERROR", "Exception异常", ex)
                    .others("msg", ex.getMessage())
            );
        }
    }
}
