package com.zeroflow.exception;

import com.zeroflow.conf.FlowErrEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 可重试异常，入库错误日志表，并执行重试机制
 *
 * @author richard.chen
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class RetryException extends Exception {

    private static final long serialVersionUID = 3919541453510867375L;
    private Object context = null;
    private List<String> commandRecord = new ArrayList<String>();
    private String exceptionCommand = "";
    private int code = FlowErrEnum.ERROR.code();
    private String message = FlowErrEnum.ERROR.msg();


    public RetryException() {
        super();
    }

    public RetryException(String msg, int code) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public RetryException(Throwable cause, String msg, int code) {
        super(msg, cause);
        this.message = msg;
        this.code = code;
    }

    public void of(String exceptionCommand, List<String> commandRecord, Object context) {
        this.setExceptionCommand(exceptionCommand);
        this.setCommandRecord(commandRecord);
        this.setContext(context);
    }

}
