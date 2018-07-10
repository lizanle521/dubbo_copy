package com.lizanle.dubbo.common.copy.extension.ext8_add.impl;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;
import com.lizanle.dubbo.common.copy.extension.ext8_add.AddExt1;


public class AddExt1_ManualAdd1 implements AddExt1 {
    @Override
    public String echo(URL url, String s) {
        return getClass().getSimpleName();
    }
}
