package com.lizanle.dubbo.common.copy.compiler.support;

import java.net.URI;
import java.net.URISyntaxException;

public class ClassUtils {
    public static final String CLASS_EXTENSIONS = ".class";

    public static final String JAVA_EXTENSIONS = ".java";

    private static final int JIT_LIMIT = 5 * 1024;

    private ClassUtils() {
    }

    public static Class<?> forName(String className){
        try {
            return _forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(),e);
        }
    }

    public static Class<?> forName(String[] packages,String className){
        try{
            return _forName(className);
        }catch (ClassNotFoundException e){
            if(packages != null && packages.length > 0){
                for (String pkg : packages) {
                    try {
                        return _forName(pkg+"."+className);
                    } catch (ClassNotFoundException e1) {

                    }
                }
            }
            throw new IllegalStateException(e.getMessage(),e);
        }
    }

    public static Class<?> _forName(String className) throws ClassNotFoundException {
        if("boolean".equals(className)){
            return boolean.class;
        }
        if("byte".equals(className)){
            return byte.class;
        }
        if("char".equals(className)){
            return char.class;
        }
        if("short".equals(className)){
            return short.class;
        }
        if("int".equals(className)){
            return int.class;
        }
        if("long".equals(className)){
            return long.class;
        }
        if("float".equals(className)){
            return float.class;
        }
        if("double".equals(className)){
            return double.class;
        }

        if("boolean[]".equals(className)){
            return boolean[].class;
        }
        if("byte[]".equals(className)){
            return byte[].class;
        }
        if("char[]".equals(className)){
            return char[].class;
        }
        if("short[]".equals(className)){
            return short[].class;
        }
        if("int[]".equals(className)){
            return int[].class;
        }
        if("long[]".equals(className)){
            return long[].class;
        }
        if("float[]".equals(className)){
            return float[].class;
        }
        if("double[]".equals(className)){
            return double[].class;
        }
        try{
            return arrayForName(className);
        }catch (ClassNotFoundException e){
            // 尝试 java.lang包
            if(className.indexOf(".") == -1){
                try{
                    return arrayForName("java.lang."+className);
                }catch (ClassNotFoundException t){
                    // 忽略尝试异常
                }
            }
            throw  e;
        }
    }

    private static Class<?> arrayForName(String className) throws ClassNotFoundException{
        return Class.forName(className.endsWith("[]")?
          "[L"+className.substring(0,className.length()-2)+";" :
                className,true,Thread.currentThread().getContextClassLoader()
        );
    }

    public static URI toURI(String s) {
        try {
            return new URI(s);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
