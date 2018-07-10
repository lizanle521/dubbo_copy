package com.lizanle.dubbo.common.copy.extension.ext8_add;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.SPI;

@SPI("impl1")
public interface AddExt2 {
    @Adaptive
    String echo(URL url,String s);
}
