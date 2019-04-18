package com.zeroflow.base;

import com.zeroflow.annotation.Unit;
import com.zeroflow.bean.ErrorLog;
import com.zeroflow.bean.FlowResult;
import com.zeroflow.bean.tuple.FiveTuple;
import com.zeroflow.bean.tuple.TwoTuple;
import com.zeroflow.conf.FlowErrEnum;
import com.zeroflow.exception.CriticalException;
import com.zeroflow.exception.DiscardException;
import com.zeroflow.exception.RetryException;
import com.zeroflow.threadpool.FlowThreadPool;
import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:基础流程管理器
 * @date:2019/4/8
 */
@Slf4j
public abstract class BaseFlowHandler<D extends BaseContext> {
    private EnhanceLogger elog = EnhanceLogger.of(log);
    //自定义的流程注解信息<Method,流程名称，是否开启异步，前置检查列表>
    private static Map<String, Map<String, FiveTuple<Method, String, Boolean, Boolean, String[]>>> registerUnit = new ConcurrentHashMap<>();
    //命令执行顺序
    private static Map<String, List<String>> unitOrder = new ConcurrentHashMap<>();
    //上下文数据
    private D context;
    //重试流程标识
    private boolean retryFlag;
    //流程执行器
    private BaseFlowLogHandler flowLogHandler;
    //已执行的命令列表
    private List<String> commandRecord = new ArrayList<String>();
    //错误日志
    private ErrorLog errorLog = null;
    //异步线程
    private static Executor EXECUTOR = FlowThreadPool.getThreadPool();

    /**
     * 初始化handle器
     */
    public BaseFlowHandler() {
        this.retryFlag = false;
        registerUnit();
    }

    /**
     * 解释自定义注解
     */
    private void registerUnit() {
        if (null == registerUnit.get(this.getClass().getName())) {
            synchronized (BaseFlowHandler.class) {
                if (null == registerUnit.get(this.getClass().getName())) {
                    Map<String, FiveTuple<Method, String, Boolean, Boolean, String[]>> flowUnit = new ConcurrentHashMap<>();
                    ArrayList<TwoTuple<String, Integer>> orderList = new ArrayList();
                    Class finalSuperClass = this.getClass();
                    //历遍包含父类中含有Unit的配置方法
                    while (finalSuperClass.getSuperclass() != Object.class) {
                        Method[] declaredMethods = finalSuperClass.getMethods();
                        for (Method method : declaredMethods) {
                            if (method.isAnnotationPresent(Unit.class)) {
                                Unit annotation = method.getAnnotation(Unit.class);
                                //单元名称
                                String name = annotation.name();
                                //命令执行顺序
                                Integer order = annotation.order();
                                //是否异步
                                boolean asyn = annotation.asyn();
                                //是否启用流程单元
                                boolean enable = annotation.enable();
                                //前置检查条件
                                String[] preCheck = annotation.preCheck();
                                FiveTuple<Method, String, Boolean, Boolean, String[]> unit = new FiveTuple<>(method, name, asyn, enable, preCheck);
                                if (null == flowUnit.get(name)) {
                                    flowUnit.put(name, unit);
                                    if (enable) {
                                        orderList.add(new TwoTuple(name, order));
                                    }
                                }
                            }
                        }
                        finalSuperClass = finalSuperClass.getSuperclass();
                    }
                    registerUnit.put(this.getClass().getName(), flowUnit);
                    //生成执行命令顺序
                    Collections.sort(orderList, (TwoTuple<String, Integer> x, TwoTuple<String, Integer> y) -> x.second > y.second ? 1 : -1);
                    List<String> execCommandList = new ArrayList<String>();
                    for (TwoTuple<String, Integer> value : orderList) {
                        execCommandList.add(value.first);
                    }
                    unitOrder.put(this.getClass().getName(), execCommandList);
                }
                elog.info(LogEvent.of("BaseFlowHandler-registerUnit", this.getClass().getName() + "注册流程单元信息成功"));
            }
        }
    }


    protected List<String> getCommandList() {
        return unitOrder.get(this.getClass().getName());
    }

    /**
     * 获取上下文数据
     *
     * @return
     */
    public D getContext() {
        return this.context;
    }

    /**
     * 返回线程池
     *
     * @return
     */
    protected Executor getExecutor() {
        return EXECUTOR;
    }

    /**
     * 设置上下文数据
     *
     * @param context
     * @return
     */
    public BaseFlowHandler setContext(D context) {
        this.context = context;
        return this;
    }

    public BaseFlowHandler setFlowLogHandler(Class<? extends BaseFlowLogHandler> clz) {
        try {
            this.flowLogHandler = (BaseFlowLogHandler) clz.newInstance();
        } catch (Exception ex) {
            elog.error(LogEvent.of("BaseFlowHandler-setFlowLogHandler", "初始化FlowLogHandler异常", ex)
                    .analyze("userId", context.getUserID())
                    .analyze("unique_code", context.getUniqueCode())
                    .analyze("flow_name", context.getFlowName())
            );
        }
        return this;
    }

    public BaseFlowHandler setFlowLogHandler(BaseFlowLogHandler flowLogHandler) {
        this.flowLogHandler = flowLogHandler;
        return this;
    }


    /**
     * 重试流程初始化handle器
     *
     * @param commandRecord 已执行的命令
     * @param errorLog      重试日志信息
     */
    public BaseFlowHandler setRetryParam(List<String> commandRecord, ErrorLog errorLog) {
        this.commandRecord = commandRecord;
        this.errorLog = errorLog;
        this.retryFlag = true;
        return this;
    }

    /**
     * 返回的结果
     *
     * @return
     */
    protected abstract FlowResult<?> getResult();

    /**
     * 执行流程
     *
     * @return
     * @throws Exception
     */
    public FlowResult invoke() throws Exception {
        return flowLogHandler.invoke(this, errorLog);
    }

    /**
     * 执行流程命令列表
     *
     * @throws CriticalException
     * @throws RetryException
     * @throws DiscardException
     */
    public FlowResult execCommandList() throws CriticalException, RetryException, DiscardException {
        List<String> commandList = getCommandList();
        elog.info(LogEvent.of("BaseFlowHandler-execCommandList", "执行命令列表")
                .analyze("userId", context.getUserID())
                .analyze("unique_code", context.getUniqueCode())
                .analyze("flow_name", context.getFlowName())
                .others("commandList", commandList)
        );
        for (String command : commandList) {
            execCommand(command);
        }
        return getResult();
    }

    /**
     * 异步流程执行包装器
     *
     * @param command
     * @throws Exception
     */
    private void asynFlowInvokerWrapper(String command) {
        //构建一个已全部完成的命令列表，只将其中删除，重试时即仅重试当前命令
        List<String> commandRecord = new ArrayList<String>(getCommandList());
        commandRecord.remove(command);
        getExecutor().execute(() -> {
            flowLogHandler.asynInvoke(this, command, commandRecord);
        });
    }

    /**
     * 异步执行指定命令
     *
     * @param command
     * @throws CriticalException
     * @throws RetryException
     * @throws DiscardException
     */
    public void asynInvoke(String command, List<String> commandRecord) throws CriticalException, RetryException, DiscardException {
        asynExecCommand(command, commandRecord);
    }

    /**
     * 前置处理
     *
     * @param command
     * @throws CriticalException
     * @throws RetryException
     * @throws DiscardException
     */
    protected void beforeCommand(String command) throws CriticalException, RetryException, DiscardException {
    }

    /**
     * 后置处理
     *
     * @param command
     * @throws CriticalException
     * @throws RetryException
     * @throws DiscardException
     */
    protected void afterCommand(String command) {
    }

    /**
     * 调用命令
     */
    private void execCommand(String command) throws CriticalException, RetryException, DiscardException {
        elog.info(LogEvent.of("execCommand", "执行命令:" + command)
                .analyze("userId", context.getUserID())
                .analyze("unique_code", context.getUniqueCode())
                .analyze("flow_name", context.getFlowName())
                .analyze("command", command)
        );
        try {
            if (retryFlag && commandRecord.contains(command)) {
                elog.info(LogEvent.of("BaseFlowHandler-execCommand", "重试流程：" + command + "命令已执行")
                        .analyze("userId", context.getUserID())
                        .analyze("unique_code", context.getUniqueCode())
                        .analyze("flow_name", context.getFlowName())
                        .analyze("command", command)
                );
                return;
            }

            FiveTuple<Method, String, Boolean, Boolean, String[]> unit = registerUnit.get(this.getClass().getName()).get(command);
            ;
            if (null == unit || null == unit.first) {
                throw new CriticalException("找不到配置的命令名称:" + command, FlowErrEnum.BIZ_ERROR.code());
            }
            //前置条件检查
            for (String pre : unit.five) {
                if (!commandRecord.contains(pre)) {
                    throw new CriticalException(command + "的前置条件:" + pre + "未完成", FlowErrEnum.BIZ_ERROR.code());
                }
            }
            //开启异步，重试不开启
            if (!retryFlag && unit.third) {
                elog.info(LogEvent.of("BaseFlowHandler-execCommand", command + "开启异步执行")
                        .analyze("userId", context.getUserID())
                        .analyze("unique_code", context.getUniqueCode())
                        .analyze("flow_name", context.getFlowName())
                        .analyze("command", command)
                );
                try {
                    asynFlowInvokerWrapper(command);
                    commandRecord.add(command);
                    return;
                } catch (Exception ex) {
                    elog.error(LogEvent.of("BaseFlowHandler-asynFlowInvokerWrapper", "加入异步线程池失败,以同步方式执行", ex)
                            .analyze("userId", context.getUserID())
                            .analyze("unique_code", context.getUniqueCode())
                            .analyze("flow_name", context.getFlowName())
                            .analyze("command", command)
                    );
                }
            }
            reflectInvoke(command);
        } catch (CriticalException ex) {
            ex.of(command, this.commandRecord, this.context);
            throw ex;
        } catch (RetryException ex) {
            ex.of(command, this.commandRecord, this.context);
            throw ex;
        } catch (DiscardException ex) {
            ex.of(command, this.commandRecord, this.context);
            throw ex;
        } catch (Exception ex) {
            CriticalException unknowExcepton = new CriticalException(ex, ObjectUtils.defaultIfNull(ex.getMessage(), "unknow异常"), FlowErrEnum.ERROR.code());
            unknowExcepton.of(command, this.commandRecord, this.context);
            throw unknowExcepton;
        }
    }


    /**
     * 异步执行
     *
     * @param command
     * @param commandRecord
     * @throws CriticalException
     * @throws RetryException
     * @throws DiscardException
     */
    private void asynExecCommand(String command, List commandRecord) throws CriticalException, RetryException, DiscardException {
        try {
            reflectInvoke(command);
        } catch (CriticalException ex) {
            ex.of(command, commandRecord, this.context);
            throw ex;
        } catch (RetryException ex) {
            ex.of(command, commandRecord, this.context);
            throw ex;
        } catch (DiscardException ex) {
            ex.of(command, commandRecord, this.context);
            throw ex;
        } catch (Exception ex) {
            CriticalException unknowExcepton = new CriticalException(ex, ObjectUtils.defaultIfNull(ex.getMessage(), "unknow异常"), FlowErrEnum.ERROR.code());
            unknowExcepton.of(command, commandRecord, this.context);
            throw unknowExcepton;
        }
    }

    private void reflectInvoke(String command) throws CriticalException, RetryException, DiscardException {
        try {
            FiveTuple<Method, String, Boolean, Boolean, String[]> unit = registerUnit.get(this.getClass().getName()).get(command);
            beforeCommand(command);
            unit.first.invoke(this);
            commandRecord.add(command);
            afterCommand(command);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof CriticalException) {
                throw (CriticalException) ex.getTargetException();
            } else if (ex.getTargetException() instanceof RetryException) {
                throw (RetryException) ex.getTargetException();
            } else if (ex.getTargetException() instanceof DiscardException) {
                throw (DiscardException) ex.getTargetException();
            } else {
                elog.error(LogEvent.of("BaseFlowHandler-reflectInvoke", "反射调用异常", ex)
                        .analyze("userId", context.getUserID())
                        .analyze("unique_code", context.getUniqueCode())
                        .analyze("flow_name", context.getFlowName())
                );
                throw new CriticalException(FlowErrEnum.REFLECT_INVOKE_ERROR);
            }
        } catch (Exception ex) {
            elog.error(LogEvent.of("BaseFlowHandler-reflectInvoke", "反射调用异常", ex)
                    .analyze("userId", context.getUserID())
                    .analyze("unique_code", context.getUniqueCode())
                    .analyze("flow_name", context.getFlowName())
            );
            throw new CriticalException(FlowErrEnum.REFLECT_INVOKE_ERROR);
        }

    }
}
