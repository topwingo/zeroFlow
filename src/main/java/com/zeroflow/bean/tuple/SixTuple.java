package com.zeroflow.bean.tuple;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:
 * @date:2019/4/8
 */
public class SixTuple<A, B, C, D, E,F> extends FiveTuple<A, B, C, D,E> {

    public final F six;

    public SixTuple(A a, B b, C c, D d, E e,F f) {
        super(a, b, c, d,e);
        six = f;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + "," + third + "," + fourth + "," + five + "," + six + ")";
    }
}
