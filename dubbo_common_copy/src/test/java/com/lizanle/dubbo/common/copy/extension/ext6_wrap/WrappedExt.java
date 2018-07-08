package com.lizanle.dubbo.common.copy.extension.ext6_wrap;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.SPI;

/**
 * Created by lizanle on 2018/7/8.
 */
@SPI("impl1")
public interface WrappedExt {
    String echo(URL url,String s);
}
