package com.zeroflow.bizservice;

import com.zeroflow.conf.FlowErrEnum;
import com.zeroflow.exception.CriticalException;
import com.zeroflow.exception.DiscardException;
import com.zeroflow.exception.RetryException;
import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description: 测试流程
 * @date:2019/4/8
 */
@Slf4j
public class TestBiz {
    private EnhanceLogger elog = EnhanceLogger.of(log);

    public void T1(Long userId) throws CriticalException, InterruptedException {
     /*  if (true) {
            throw new CriticalException("T1测试异常-CriticalException", FlowErrEnum.ERROR.code());
        }*/
        Thread.sleep(300);
        elog.info(LogEvent.of("TestBiz", "T1-Result")
                .analyze("input-userId", userId)
        );
    }

    public String T2() throws RetryException, InterruptedException {
       /* if (true) {
            throw new RetryException("T2测试异常-RetryException", FlowErrEnum.ERROR.code());
        }*/
        Thread.sleep(50);
        String text = "hi,我是T2";
        elog.info(LogEvent.of("TestBiz", "T2-Result")
                .analyze("result", text)
        );
        return text;
    }

    public List<String> T3(String unique) throws DiscardException {
       /* if (true) {
            throw new DiscardException("T3测试异常-DiscardException", FlowErrEnum.BIZ_ERROR.code());
        }*/

        String text = "hi,我是T3";
        List<String> result = Arrays.asList("A1", "A2", "A3");
        elog.info(LogEvent.of("TestBiz", "T3-Result")
                .analyze("text", text)
                .analyze("result", result)
        );
        return result;
    }

}

