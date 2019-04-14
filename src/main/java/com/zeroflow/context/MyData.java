package com.zeroflow.context;

import com.zeroflow.base.BaseContext;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: richard.chen
 * @version: v1.0
 * @description:
 * @date:2019/4/8
 */
@Data
public class MyData extends BaseContext {
    public String t2Result="";
    public List<String > t3Result=new ArrayList<String>();
}
