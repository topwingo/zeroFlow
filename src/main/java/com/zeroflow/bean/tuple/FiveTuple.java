package com.zeroflow.bean.tuple;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:
 * @date:2019/4/8
 */
public class FiveTuple<A, B, C, D, E> extends FourTuple<A, B, C, D> {

    public final E five;

    public FiveTuple(A a, B b, C c, D d, E e) {
        super(a, b, c, d);
        five = e;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + "," + third + "," + fourth + "," + five + ")";
    }
}
