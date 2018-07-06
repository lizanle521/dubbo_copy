package com.lizanle.dubbo.common.copy.extension.ext1;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.SPI;

@SPI("impl1")
public interface SimpleExt {
    //没有使用key的adaptive
    @Adaptive
    String echo(URL url,String s);

    @Adaptive({"key1","key2"})
    String yell(URL url,String s);

    String bang(URL url,int i);
}
