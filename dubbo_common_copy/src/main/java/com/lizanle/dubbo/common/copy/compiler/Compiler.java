package com.lizanle.dubbo.common.copy.compiler;

import com.lizanle.dubbo.common.copy.extension.SPI;

@SPI("javassist")
public interface Compiler {
    /**
     * 编译java源码
     * @param code
     * @param classLoader
     * @return
     */
    Class<?> compile(String code,ClassLoader classLoader);
}
