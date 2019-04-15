package com.zeroflow.handler;

import com.zeroflow.annotation.Unit;
import com.zeroflow.base.BaseFlowHandler;
import com.zeroflow.bean.FlowResult;
import com.zeroflow.bizservice.TestBiz;
import com.zeroflow.context.MyData;
import com.zeroflow.exception.CriticalException;
import com.zeroflow.exception.DiscardException;
import com.zeroflow.exception.RetryException;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:测试流程管理器
 * @date:2019/4/8
 */

public class T2FlowHandle extends TFlowHandle {

    private TestBiz flow = new TestBiz();

    public T2FlowHandle() {
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


    @Unit(name = "T1", enable = true, order = 1, asyn = true)
    public void T1() throws InterruptedException, CriticalException {
        System.out.println("########T4");
        flow.T1(3333L);
    }

    @Unit(name = "T5", order = 8, asyn = false)
    public void T5() throws RetryException, InterruptedException {
        this.getContext().setT2Result(flow.T2());
    }

    @Unit(name = "T6", order = 9)
    public void T6() throws DiscardException {
        System.out.println("########T6");
        this.getContext().setT3Result(flow.T3("T0001"));
    }

}
