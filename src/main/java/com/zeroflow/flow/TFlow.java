package com.zeroflow.flow;

import com.zeroflow.conf.FlowErrEnum;
import com.zeroflow.exception.CriticalException;

import java.util.Arrays;
import java.util.List;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description: 测试流程
 * @date:2019/4/8
 */
public class TFlow  {
      public void T1(Long userId) throws CriticalException, InterruptedException {
        System.out.println("hi,我是T1:userId=" + userId);
        Thread.sleep(300);
/*
        System.out.println("T1 Delay 5s");
        System.out.println("T1 finished");
*/

       //   throw new CriticalException("测试异步的异常", FlowErrEnum.BIZ_ERROR.code());

    }

    public String T2() throws CriticalException {
        //throw new CriticalException("测试我是T2:CriticalException", FlowErrEnum.BIZ_ERROR.code());
        String text = "hi,我是T2";
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(text);
        return text;

    }

    public List<String> T3(String unique) {
        System.out.println("hi,我是T3" );
        return Arrays.asList("A1", "A2", "A3");
    }

}

