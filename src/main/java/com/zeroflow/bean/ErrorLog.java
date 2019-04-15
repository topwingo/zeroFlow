package com.zeroflow.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 错误日志数据
 *
 * @author richard.chen
 */
@Data
public class ErrorLog {
    private long id;
    private long user_id;
    private String unique_code;
    private String flowName;
    private int code;
    private String message;
    private String exception_command;
    private String context;
    private String command_record;
    //0已重新执行并完成，1可重试,2不可重试
    private int type = 0;
    //重试次数
    private int retry_num = 0;

    public void of(int code, String message, long user_id, String unique_code, String flowName, String context, String exception_command, String command_record, int type, int retry_num) {
        this.code = code;
        this.user_id=user_id;
        this.unique_code=ObjectUtils.defaultIfNull(unique_code, StringUtils.EMPTY);
        this.message = ObjectUtils.defaultIfNull(message, StringUtils.EMPTY);
        this.context = ObjectUtils.defaultIfNull(context, StringUtils.EMPTY);
        this.exception_command = ObjectUtils.defaultIfNull(exception_command, StringUtils.EMPTY);
        this.command_record = ObjectUtils.defaultIfNull(command_record, StringUtils.EMPTY);
        this.type = type;
        this.retry_num = retry_num;
    }

}
