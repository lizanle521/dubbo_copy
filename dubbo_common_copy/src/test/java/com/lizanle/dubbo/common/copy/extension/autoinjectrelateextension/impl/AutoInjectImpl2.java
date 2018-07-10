package com.lizanle.dubbo.common.copy.extension.autoinjectrelateextension.impl;

import com.lizanle.dubbo.common.copy.extension.autoinjectrelateextension.AutoInjectRelateExtension;

import java.util.ArrayList;
import java.util.List;

public class AutoInjectImpl2 implements AutoInjectRelateExtension {
    private List<AutoInjectRelateExtension> list = new ArrayList<>();
    @Override
    public void setAutoInjectRelateExtension(AutoInjectRelateExtension injectRelateExtension) {
        list.add(injectRelateExtension);
    }
    @Override
    public int getListSize() {
        return list.size();
    }
}
