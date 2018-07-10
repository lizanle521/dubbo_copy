package com.lizanle.dubbo.common.copy.extension.factory;

import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.ExtensionFactory;
import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Adaptive
public class AdaptiveExtensionFactory implements ExtensionFactory {
    private final List<ExtensionFactory> factories;

    public AdaptiveExtensionFactory() {
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        List<ExtensionFactory> list = new ArrayList<>();
        for (String s : loader.getSupportedExtensions()) {
            list.add(loader.getExtension(s));
        }
        factories = Collections.unmodifiableList(list);
    }

    @Override
    public <T> T getExtension(Class<T> tClass, String name) {
        for (ExtensionFactory factory : factories) {
            T extension = factory.getExtension(tClass, name);
            if(extension !=null){
                return extension;
            }
        }
        return null;
    }
}
