package com.lizanle.dubbo.common.copy.extension.activate.impl;

import com.lizanle.dubbo.common.copy.extension.Activate;
import com.lizanle.dubbo.common.copy.extension.activate.ActivateExt1;

@Activate(group = "default_group")
public class ActivateExt1Impl1 implements ActivateExt1 {
    @Override
    public String echo(String msg) {
        return msg;
    }
}