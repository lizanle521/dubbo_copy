package com.lizanle.dubbo.common.copy.utils;

/**
 * 保存一个任意类型值的帮助类
 * @param <T>
 */
public class Holder<T> {
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
