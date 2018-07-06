package com.lizanle.dubbo.common.copy.extension;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
    /**
     * 缺省扩展点名称
     * @return 缺省扩展点名称
     */
    String value() default "";
}
