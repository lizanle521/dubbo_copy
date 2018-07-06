package com.lizanle.dubbo.common.copy.beanutil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:491823689@qq.com">lizanle</a>
 */
public final class JavaBeanDescriptor implements Serializable,Iterable<Map.Entry<Object,Object>> {
    public static final int TYPE_CLASS = 1;
    public static final int TYPE_ENUM = 2;
    public static final int TYPE_COLLECTION = 3;
    public static final int TYPE_MAP = 4;
    public static final int TYPE_ARRAY = 5;
    /**
     * 原始类型，或者String 或者 Boolean 或者 Character 或者 Number 或者 Date
     */
    public static final int TYPE_PRIMITIVE = 6;
    public static final int TYPE_BEAN = 7;
    private static final long serialVersionUID = -2323753515814774549L;

    private static final String ENUM_PROPERTY_NAME = "name";

    private static final String CLASS_PROPERTY_NAME = "name";

    private static final String PRIMITIVE_PROPERTY_VALUE = "value";

    /**
     * 定义个这个常亮仅用来判断类型是否合法
     */
    private final static int TYPE_MAX = TYPE_BEAN;
    private final static int TYPE_MIN = TYPE_CLASS;

    private String className;

    private int type;

    private Map<Object,Object> properties = new LinkedHashMap<>();

    public JavaBeanDescriptor( ) {

    }

    public JavaBeanDescriptor(String className, int type) {
        notEmpty(className,"class name is empty");
        if(!isValidType(type)){
            throw new IllegalArgumentException(new StringBuilder(16)
                    .append("type [").append(type).append(" ] is not supported").toString());
        }
        this.className = className;
        this.type = type;
    }

    public boolean isClassType(){
        return TYPE_CLASS == type;
    }

    public boolean isEnumType(){
        return TYPE_ENUM == type;
    }

    public boolean isCollectionType(){
        return TYPE_COLLECTION == type;
    }

    public boolean isMapType(){
        return TYPE_MAP == type;
    }

    public boolean isArrayType(){
        return TYPE_ARRAY == type;
    }

    public boolean isPrimitiveType(){
        return TYPE_PRIMITIVE == type;
    }

    public boolean isBeanType(){
        return TYPE_BEAN == type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object setProperty(Object propertyName,Object propertyValue){
        notNull(propertyName,"property name is null");

        Object oldValue = properties.put(propertyName, propertyValue);
        return oldValue;
    }

    public Object getProperty(Object propertyName){
        notNull(propertyName,"property name is null");

        Object oldValue = properties.get(propertyName);
        return oldValue;
    }

    public boolean containsProperty(Object propertyName){
        notNull(propertyName,"property name is null");
        return properties.containsKey(propertyName);
    }

    public String setEnumNameProperty(String name){
        if(isEnumType()){
            Object result = setProperty(ENUM_PROPERTY_NAME, name);
            return result == null ? null : result.toString();
        }
        throw new IllegalArgumentException("The instance is not a enum wrapper");
    }

    public Object getEnumNameProperty(){
        if(isEnumType()){
            Object property = getProperty(ENUM_PROPERTY_NAME);
            return property;
        }
        throw new IllegalArgumentException("The instance is not a enum wrapper");
    }

    public String setClassNameProperty(String name){
        if(isClassType()){
            Object result = setProperty(CLASS_PROPERTY_NAME, name);
            return result == null ? null : result.toString();
        }
        throw new IllegalArgumentException("The instance is not a class wrapper");
    }

    public String  getClassNameProperty(){
        if(isClassType()){
            Object property = getProperty(CLASS_PROPERTY_NAME);
            return property == null ? null : property.toString();
        }
        throw new IllegalArgumentException("The instance is not a class wrapper");
    }

    public Object getPrimitiveProperty(){
        if(isPrimitiveType()){
            Object property = getProperty(PRIMITIVE_PROPERTY_VALUE);
            return property;
        }
        throw new IllegalArgumentException("The instance is not a class wrapper");
    }

    public Object setPrimitiveProperty(Object name){
        if(isPrimitiveType()){
            return setProperty(PRIMITIVE_PROPERTY_VALUE, name);
        }
        throw new IllegalArgumentException("The instance is not a class wrapper");
    }

    public Iterator<Map.Entry<Object, Object>> iterator() {
        return properties.entrySet().iterator();
    }

    public int propertySize() {
        return properties.size();
    }

    private void notNull(Object obj,String message){
        if(obj == null){
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isValidType(int type){
        return TYPE_MIN <= type && TYPE_MAX >= type;
    }

    private void notEmpty(String string,String message){
        if(isEmpty(string)){
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isEmpty(String string){
        return string == null || "".equals(string);
    }
}
