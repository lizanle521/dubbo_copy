package com.lizanle.dubbo.common.copy.extension.activate;

import com.lizanle.dubbo.common.copy.extension.SPI;

@SPI("impl1")
public interface ActivateExt1 {
    String echo(String msg);
}
