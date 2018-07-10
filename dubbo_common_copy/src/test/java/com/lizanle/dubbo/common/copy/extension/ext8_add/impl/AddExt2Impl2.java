package com.lizanle.dubbo.common.copy.extension.ext8_add.impl;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.ext8_add.AddExt2;

public class AddExt2Impl2 implements AddExt2 {
    @Override
    public String echo(URL url, String s) {
        return getClass().getSimpleName();
    }
}
