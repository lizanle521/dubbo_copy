package com.lizanle.dubbo.common.copy.extension.support;

import com.lizanle.dubbo.common.copy.extension.Activate;
import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;
import com.lizanle.dubbo.common.copy.extension.SPI;

import java.util.Comparator;

public class ActivateComparator implements Comparator<Object> {
    public static final Comparator<Object> COMPARATOR = new ActivateComparator();

    @Override
    public int compare(Object o1, Object o2) {
        if(o1 == null && o2 == null){
            return 0;
        }
        if(o1 == null){
            return -1;
        }
        if(o2 == null){
            return 1;
        }
        if(o1.equals(o2)){
            return 0;
        }
        Activate a1 = o1.getClass().getAnnotation(Activate.class);
        Activate a2 = o2.getClass().getAnnotation(Activate.class);
        if((a1.before().length > 0 || a1.after().length>0 || a2.before().length>0 || a2.after().length >0)
                && a1.getClass().getInterfaces().length>0
                && a1.getClass().getInterfaces()[0].isAnnotationPresent(SPI.class)){
            ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(a1.getClass().getInterfaces()[0]);
            if(a1.before().length > 0 || a1.after().length > 0){
                String extensionName = extensionLoader.getExtensionName(a2.getClass());
                for (String s : a1.before()) {
                    if(s.equals(extensionName)){
                        return -1;
                    }
                }
                for (String s : a1.after()) {
                    if(s.equals(extensionName)){
                        return 1;
                    }
                }
            }
            if(a2.before().length > 0 || a2.after().length > 0){
                String extensionName = extensionLoader.getExtensionName(a1.getClass());
                for (String s : a2.before()) {
                    if(s.equals(extensionName)){
                        return 1;
                    }
                }
                for (String s : a2.after()) {
                    if(s.equals(extensionName)){
                        return -1;
                    }
                }
            }

        }
        int order1 = a1 == null ? 0 : a1.order();
        int order2 = a2 == null ? 0 : a2.order();
        return order1 > order2 ? 1 : -1 ; // 不能为0，防止在HastSet等集合中，当做同一个元素覆盖
    }
}
