package com.lizanle.dubbo.common.copy.util;

import com.lizanle.dubbo.common.copy.utils.LRUCache;
import org.junit.Test;

import java.util.Map;

public class LRUCacheTest {
    @Test
    public void testLRU() {
        LRUCache<String, String> cache = new LRUCache<>(4);
        cache.put("1","1");
        cache.put("2","2");
        cache.put("3","3");
        cache.put("4","4");

        for (Map.Entry<String, String> entry : cache.entrySet()) {
            System.out.println(entry.getKey());
        }
        cache.get("3");
        /**
             * |------|  <--before-- |---------|  <--before--  |--------|  <--before--  |---------|
           head|   1  |              |    2    |               |    3   |               |    4    |tail
             * |------|  --after-->  |---------|  --after-->   |--------|  --after-->   |---------|
         *
         * 调用get方法后
         * 1. 首先判断是不是获取到了尾部
         * 2. 如果不是的话，那么就开始了一轮 将最新访问的元素放到 尾部的过程
         * 3. 过程很简单，就是将元素从双向链表中切出来，然后接到尾巴上
         */
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            System.out.println(entry.getKey());
        }
    }
}
