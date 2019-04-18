package com.zeroflow.exception;

import com.zeroflow.conf.FlowErrEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 丢弃异常，直接从当前流程中丢弃掉，错误不入库错误日志表
 *
 * @author richard.chen
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DiscardException extends Exception {

    private static final long serialVersionUID = -672178787968848999L;
    private Object context = null;
    private List<String> commandRecord = new ArrayList<String>();
    private String exceptionCommand = "";
    private int code = FlowErrEnum.ERROR.code();
    private String message = FlowErrEnum.ERROR.msg();
    //流程返回结果，用于幂等结果返回
    private Object data;
    public DiscardException() {
        super();
    }

    public DiscardException(String msg, int code) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public DiscardException(Throwable cause, String msg, int code) {
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
