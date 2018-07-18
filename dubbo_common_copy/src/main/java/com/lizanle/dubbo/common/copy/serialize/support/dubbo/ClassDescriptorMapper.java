package com.lizanle.dubbo.common.copy.serialize.support.dubbo;

public interface ClassDescriptorMapper {

    String getDescriptor(int index);

    int getDescriptorIndex(String desc);
}
