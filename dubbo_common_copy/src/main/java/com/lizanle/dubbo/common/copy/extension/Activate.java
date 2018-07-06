package com.lizanle.dubbo.common.copy.extension;

import java.lang.annotation.*;

/**
 * Activate
 * <p/>
 * 对于可以被框架自动激活加载的的扩展，此Annotation用于配置扩展被自动激活的加载条件
 * 比如，过滤扩展，有多个实现，使用Activate Annotation可以根据条件自动加载
 * <ol>
 *     <li>{@link Activate#group()} 生效的group,具体有哪些group由框架SPI给出。
 *     <li>{@link Activate#value()} 在{com.lizanle.dubbo.common.copy.URL}中的key集合中有，则生效
 * </ol>
 * <p>
 *
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface Activate {
    /**
     * group过滤条件
     * <br/>
     * 如果包含{@link ExtensionLoader#getActivateExtension} 方法中group参数给的值，则返回扩展
     * @return
     */
    String[] group() default {};

    /**
     * Key 过滤条件。包含{@link ExtensionLoader#getActivateExtension} 的URL的参数key值中有，则返回过滤
     * <p/>
     * 示例:<br/>
     * 注解的值 <code>@Activate("cache","validation")</code>,
     * 则{@link ExtensionLoader#getActivateExtension} 的URL的参数中有<code>cache</code>Key,或者 <code>validation</code>,则返回扩展
     * <br/>
     * 如果没有设置，则不过滤
     * @return
     */
    String[] value() default {};

    /**
     * 排序信息，可以不提供
     * @return
     */
    String[] before() default {};

    /**
     * 排序信息，可以不提供
     * @return
     */
    String[] after() default {};

    /**
     * 排序信息，可以不提供
     * @return
     */
    int order() default 0;
}
