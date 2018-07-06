package com.lizanle.dubbo.common.copy.beanutil;

/**
 * @author <a href="mailto:491823689@qq.com">lizanle</a>
 */
public enum  JavaBeanAccessor {
    /**
     * Field accessor
     */
    FIELD,
    /**
     * Method accessor
     */
    METHOD,
    /**
     * Method prefer to field
     */
    ALL
    ;

    public static boolean isAccessByMethod(JavaBeanAccessor javaBeanAccessor){
        return METHOD.equals(javaBeanAccessor) || ALL.equals(javaBeanAccessor);
    }

    public static boolean isAccessByField(JavaBeanAccessor javaBeanAccessor){
        return FIELD.equals(javaBeanAccessor) || ALL.equals(javaBeanAccessor);
    }

}
