package com.lizanle.dubbo.common.copy.extension.ext8_add.impl;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;
import com.lizanle.dubbo.common.copy.extension.ext8_add.AddExt2;

@Adaptive
public class AddExt2_ManualAdaptive implements AddExt2 {
    @Override
    public String echo(URL url, String s) {
        AddExt2 impl1 = ExtensionLoader.getExtensionLoader(AddExt2.class).getExtension("add.ext2");
        return impl1.echo(url,s);
    }
}
