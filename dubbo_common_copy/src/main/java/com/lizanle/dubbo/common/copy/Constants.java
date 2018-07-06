package com.lizanle.dubbo.common.copy;

import java.util.regex.Pattern;

/**
 * 常量
 * @author lizanle
 */
public class Constants {
    /**
     * URL 参数可以带默认前缀 default.
     */
    public final static String DEFAULT_KEY_PREFIX = "default.";

    /**
     * URL的参数值可以用 , 空白 +等进行分隔
     */
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static final String REMOVE_VALUE_PREFIX = "-";
    public static final String DEFAULT_KEY = "default";
}
