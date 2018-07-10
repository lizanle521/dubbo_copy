package com.lizanle.dubbo.common.copy.extension.ext6_wrap.impl;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.WrappedExt;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018/7/8.
 */
public class Ext5Wrapper2 implements WrappedExt {
    public static AtomicInteger echoCount = new AtomicInteger();
    WrappedExt wrappedExt;

    public Ext5Wrapper2(WrappedExt wrappedExt) {
        this.wrappedExt = wrappedExt;
    }

    @Override
    public String echo(URL url, String s) {
        echoCount.incrementAndGet();
        return wrappedExt.echo(url,s);
    }
}
