package com.zeroflow.handler;

import com.alibaba.fastjson.JSON;
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

public class TFlowHandle<C> extends BaseFlowHandler<MyData> {
    private C A;
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


    @Unit(name = "T1", order = 1, asyn = false)
    public void T1() throws InterruptedException, CriticalException {
        System.out.println("########T1");
        flow.T1(123456L);
    }

    @Unit(name = "T2", order = 2,asyn = false, preCheck = {"T1"})
    public void T2() throws RetryException, InterruptedException {
        System.out.println("########T2");
        this.getContext().setT2Result(flow.T2());
    }

    @Unit(name = "T3", order = 3, preCheck = {"T1", "T2"})
    public void T3() throws DiscardException {
        System.out.println("########T3");
        this.getContext().setT3Result(flow.T3("T0001"));
    }

}
