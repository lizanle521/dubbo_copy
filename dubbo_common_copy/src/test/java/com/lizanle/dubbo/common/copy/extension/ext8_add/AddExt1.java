package com.lizanle.dubbo.common.copy.extension.ext8_add;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.SPI;

/**
 * 编程式添加扩展点
 */
@SPI("impl1")
public interface AddExt1 {
    @Adaptive
    String echo(URL url,String s);
}
