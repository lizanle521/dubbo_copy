package com.lizanle.dubbo.common.copy.extension.factory;

import com.lizanle.dubbo.common.copy.extension.ExtensionFactory;
import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;
import com.lizanle.dubbo.common.copy.extension.SPI;

/**
 * Created by Administrator on 2018/7/8.
 */
public class SpiExtensionFactory implements ExtensionFactory {
    @Override
    public <T> T getExtension(Class<T> type, String name) {
        // 判断这个类是否是接口，并且是否有spi注解
        if(type.isInterface() && type.isAnnotationPresent(SPI.class)){
            ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            if(loader.getSupportedExtensions().size() > 0){
                return loader.getAdaptvieExtension();
            }
        }
        return null;
    }
}
