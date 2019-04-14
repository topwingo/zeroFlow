package com.zeroflow.exception;

import com.zeroflow.conf.FlowErrEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 致命异常无法重试，只入库错误日志表，不执行重试机制
 *
 * @author richard.chen
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CriticalException extends Exception {

    private static final long serialVersionUID = -6721787879688487527L;
    private Object context = null;
    private List<String> commandRecord = new ArrayList<String>();
    private String exceptionCommand = "";
    private int code = FlowErrEnum.ERROR.code();
    private String message = FlowErrEnum.ERROR.msg();

    public CriticalException() {
        super();
    }

    public CriticalException(FlowErrEnum error) {
        super(error.msg());
        this.code = error.code();
        this.message = error.msg();
    }

    public CriticalException(String msg, int code) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public CriticalException(Throwable cause, FlowErrEnum err) {
        super(err.msg(), cause);
        this.message = err.msg();
        this.code = err.code();
    }

    public CriticalException(Throwable cause, String msg, int code) {
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
