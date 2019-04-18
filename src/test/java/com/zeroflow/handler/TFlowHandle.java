package com.zeroflow.handler;

import com.zeroflow.annotation.Unit;
import com.zeroflow.base.BaseFlowHandler;
import com.zeroflow.bean.FlowResult;
import com.zeroflow.bizservice.TestBiz;
import com.zeroflow.context.MyData;
import com.zeroflow.exception.CriticalException;
import com.zeroflow.exception.DiscardException;
import com.zeroflow.exception.RetryException;
import com.zeroflow.utils.EnhanceLogger;
import com.zeroflow.utils.LogEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:测试流程管理器
 * @date:2019/4/8
 */
@Slf4j
public class TFlowHandle extends BaseFlowHandler<MyData> {
    private EnhanceLogger elog = EnhanceLogger.of(log);

    private TestBiz flow = new TestBiz();

    public TFlowHandle() {
        super();
    }

    @Override
    protected FlowResult getResult() {
        FlowResult<MyData> result = new FlowResult();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(getContext());
        return result;
    }


    @Unit(name = "T1", order = 1, asyn = true)
    public void T1() throws InterruptedException, CriticalException {
        elog.info(LogEvent.of("TFlowHandle", "######T1"));
        flow.T1(123456L);
    }

    @Unit(name = "T2", order = 2,enable = true,asyn = false, preCheck = {"T1"})
    public void T2() throws RetryException, InterruptedException {
        elog.info(LogEvent.of("TFlowHandle", "######T2"));

        this.getContext().setT2Result(flow.T2());
    }

    @Unit(name = "T3", order = 3, preCheck = {"T1", "T2"})
    public void T3() throws DiscardException {
        elog.info(LogEvent.of("TFlowHandle", "######T3"));
        this.getContext().setT3Result(flow.T3("T0001"));
    }

}
