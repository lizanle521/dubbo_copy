package com.lizanle.dubbo.common.copy.extension.ext2;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.SPI;

/**
 * 无默认扩展
 */
@SPI
public interface Ext2 {
    @Adaptive
    public String echo(URLHolder urlHolder,String s);

    String bang(URL url,int i);
}
