package com.lizanle.dubbo.common.copy.extension.ext6_wrap.impl;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.WrappedExt;

/**
 * Created by Administrator on 2018/7/8.
 */
public class Ext5Impl1 implements WrappedExt {
    @Override
    public String echo(URL url, String s) {
        return "ext5imp1-echo";
    }
}
