package com.lizanle.dubbo.common.copy.extension.autoinjectrelateextension;

import com.lizanle.dubbo.common.copy.extension.SPI;

/**
 * 测试自动注入关联扩展点
 */
@SPI
public interface AutoInjectRelateExtension {
    void setAutoInjectRelateExtension(AutoInjectRelateExtension injectRelateExtension);

    int getListSize();
}
