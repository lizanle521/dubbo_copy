package com.lizanle.dubbo.common.copy.utils;

import com.lizanle.dubbo.common.copy.compiler.support.JdkCompiler;

import java.lang.reflect.Array;
import java.util.*;

public class ClassHelper {

    private static final String ARRAY_SUFFIX = "[]";

    private static final String INTERNAL_ARRAY_PREFIX = "[L";

    private static final Map<String,Class<?>> primitiveTypeNameMap = new HashMap<>(16);

    private static final Map<Class<?>,Class<?>> primitiveWrapperTypeMap = new HashMap<>(16);

    static {
        primitiveWrapperTypeMap.put(Boolean.class,boolean.class);
        primitiveWrapperTypeMap.put(Byte.class,byte.class);
        primitiveWrapperTypeMap.put(Character.class,char.class);
        primitiveWrapperTypeMap.put(Double.class,double.class);
        primitiveWrapperTypeMap.put(Float.class,float.class);
        primitiveWrapperTypeMap.put(Integer.class,int.class);
        primitiveWrapperTypeMap.put(Long.class,long.class);
        primitiveWrapperTypeMap.put(Short.class,short.class);

        HashSet<Class<?>> primitiveTypeNames = new HashSet<>();
        primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
        primitiveTypeNames.addAll(Arrays.asList(new Class<?>[]{boolean[].class,byte[].class,
                char[].class,double[].class,float[].class,int[].class,long[].class,short.class}));
        Iterator<Class<?>> iterator = primitiveTypeNames.iterator();
        for(;iterator.hasNext();){
            Class<?> primitiveClass = iterator.next();
            primitiveTypeNameMap.put(primitiveClass.getName(),primitiveClass);
        }
    }

    public static Class<?> resolvePrimitiveClassName(String name){
        Class<?> result = null;
        if(name != null && name.length() > 0){
            result = (Class<?>)primitiveTypeNameMap.get(name);
        }
        return result;
    }

    public static ClassLoader getCallerClassLoder(Class<?> caller){
        return caller.getClassLoader();
    }

    public static Class<?> forNameWithCallerClassLoader(String name, Class<?> aClass) throws ClassNotFoundException {
        return forName(name,aClass.getClassLoader());
    }

    private static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException,LinkageError{
        Class<?> clazz = resolvePrimitiveClassName(name);
        if(clazz != null){
            return clazz;
        }
        // java.lang.String[] style array
        if(name.endsWith(ARRAY_SUFFIX)){
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(name, classLoader);
            return Array.newInstance(elementClass,0).getClass();
        }
        //[Ljava.lang.String; style array
        int internalArrayPrefixIndex = name.indexOf(INTERNAL_ARRAY_PREFIX);
        if(internalArrayPrefixIndex != -1 && name.endsWith(";")){
            String elementClassName = null;
            if(internalArrayPrefixIndex == 0){
                elementClassName = name.substring(INTERNAL_ARRAY_PREFIX.length(),name.length()-1);
            }else if(name.startsWith("[")){
                elementClassName = name.substring(1);
            }
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass,0).getClass();
        }
        ClassLoader classLoaderToUse = classLoader;
        if(classLoaderToUse == null){
            classLoaderToUse = getClassLoader();
        }
        return classLoaderToUse.loadClass(name);
    }

    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassHelper.class);
    }

    public static ClassLoader getClassLoader(Class<?> clazz){
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {

        }
        if(cl == null){
            cl = clazz.getClassLoader();
        }
        return cl;
    }
}
