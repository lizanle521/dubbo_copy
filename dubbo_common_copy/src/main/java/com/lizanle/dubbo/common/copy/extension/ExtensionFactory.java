package com.lizanle.dubbo.common.copy.extension;

@SPI
public interface ExtensionFactory {

    /**
     * 获取extension
     * @param tClass 对象类型
     * @param name 对象名称
     * @param <T>
     * @return 对象实例
     */
    <T> T getExtension(Class<T> tClass,String name);
}
