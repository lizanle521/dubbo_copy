package com.lizanle.dubbo.common.copy.extension.ext8_add.impl;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;
import com.lizanle.dubbo.common.copy.extension.ext8_add.AddExt1;

@Adaptive
public class AddExt1_ManualAdaptive implements AddExt1 {
    @Override
    public String echo(URL url, String s) {
        AddExt1 extension = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension(url.getParameter("add.ext1"));
        return extension.echo(url,s);
    }
}
