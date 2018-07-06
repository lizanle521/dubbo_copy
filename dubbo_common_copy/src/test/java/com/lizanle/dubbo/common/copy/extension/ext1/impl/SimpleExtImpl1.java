package com.lizanle.dubbo.common.copy.extension.ext1.impl;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.ext1.SimpleExt;

public class SimpleExtImpl1 implements SimpleExt {
    @Override
    public String echo(URL url, String s) {
        return "SimpleExtImpl1-echo";
    }

    @Override
    public String yell(URL url, String s) {
        return "SimpleExtImpl1-yell";
    }

    @Override
    public String bang(URL url, int i) {
        return "bang";
    }
}
