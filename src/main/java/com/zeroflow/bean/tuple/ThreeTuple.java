package com.zeroflow.bean.tuple;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:
 * @date:2019/4/8
 */
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
    public final C third;

    public ThreeTuple(A a, B b, C c) {
        super(a, b);
        this.third = c;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + "," + third +  ")";
    }
}



