package com.zeroflow.bean.tuple;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:
 * @date:2019/4/8
 */
public class FourTuple<A, B, C, D> extends ThreeTuple<A,B,C>{

    public final D fourth;

    public FourTuple(A a, B b, C c, D d) {
        super(a, b, c);
        fourth = d;
    }

    @Override
    public String toString(){
        return "(" + first + "," + second + "," + third + "," + fourth + ")";
    }
}
