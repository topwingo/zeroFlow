package com.zeroflow.handler;

import com.alibaba.fastjson.JSON;
import com.zeroflow.annotation.Unit;
import com.zeroflow.base.BaseFlowHandler;
import com.zeroflow.bean.FlowResult;
import com.zeroflow.context.MyData;
import com.zeroflow.exception.CriticalException;
import com.zeroflow.flow.TFlow;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:测试流程管理器
 * @date:2019/4/8
 */

public class TFlowHandle extends BaseFlowHandler<MyData> {

    private TFlow flow = new TFlow();

    public TFlowHandle() {
        super();
    }

    @Override
    protected FlowResult getResult() {
        FlowResult result = new FlowResult();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(JSON.toJSONString(getContext()));
        return result;
    }


    @Unit(name = "T1", order = 1, asyn = true)
    public void t1() throws InterruptedException, CriticalException {
        flow.T1(1111L);
    }

    @Unit(name = "T2", order = 2, asyn = true, preCheck = {"T1", "T2"})
    public void test2() throws CriticalException {
        this.getContext().setT2Result(flow.T2());
    }

    @Unit(name = "T3", order = 3, preCheck = {"T1", "T2"})
    public void test3() {
        this.getContext().setT3Result(flow.T3("T0001"));
        System.out.println("T3RESULT:"+this.getContext().getT2Result());
    }

}
