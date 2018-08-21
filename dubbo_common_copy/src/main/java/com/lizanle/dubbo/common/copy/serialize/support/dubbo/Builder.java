package com.lizanle.dubbo.common.copy.serialize.support.dubbo;

import com.lizanle.dubbo.common.copy.logger.Logger;
import com.lizanle.dubbo.common.copy.logger.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Builder<T> implements GenericDataFlags {

    private static final AtomicLong BUILDER_CLASS_COUNTER = new AtomicLong(0);

    private static final String BUILDER_CLASS_NAME = Builder.class.getName();

    private static final Map<Class<?>,Builder<?>> BuilderMap = new ConcurrentHashMap<>();

    private static final Map<Class<?>,Builder<?>> nonSerializableBuilderMap = new ConcurrentHashMap<>();

    private static final String FIELD_CONFIG_SUFFIX = ".fc";

    private static final int MAX_FIELD_CONFIG_FILE_SIZE = 16*1024;

    private static final Comparator<String> FNC = new Comparator<String>() {
        public int compare(String n1, String n2) {
            return compareFieldName(n1, n2);
        }
    };

    private static final Comparator<Field> FC = new Comparator<Field>() {
        public int compare(Field f1, Field f2) {
            return compareFieldName(f1.getName(), f2.getName());
        }
    };

    private static final Comparator<Constructor> CC = new Comparator<Constructor>() {
        public int compare(Constructor o1, Constructor o2) {
            return o1.getParameterTypes().length - o2.getParameterTypes().length;
        }
    };

    private final static List<String> mDescList = new ArrayList<>();

    private final static Map<String,Integer> mDescMap = new ConcurrentHashMap<>();

    public static ClassDescriptorMapper DEFAULT_CLASS_DESCRIPTOR_MAPPER = new ClassDescriptorMapper() {
        @Override
        public String getDescriptor(int index) {
            if(index < 0 || index>mDescList.size()){
                return null;
            }
            return mDescList.get(index);
        }

        @Override
        public int getDescriptorIndex(String desc) {
            Integer index = mDescMap.get(desc);
            return index == null ? -1 : index.intValue();
        }
    };
    protected static Logger logger = LoggerFactory.getLogger(Builder.class);

    private static int compareFieldName(String name1,String name2){
            int l = Math.min(name1.length(),name2.length());
        for (int i = 0; i < l; i++) {
            int t = name1.charAt(i)-name2.charAt(i);
            if(t != 0){
                return t;
            }
        }
        return name1.length() - name2.length();
    }
}
