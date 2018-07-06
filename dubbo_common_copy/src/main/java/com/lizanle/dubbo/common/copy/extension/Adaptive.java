package com.lizanle.dubbo.common.copy.extension;

import java.lang.annotation.*;

/**
 * 在{@link ExtensionLoader} 生成Extension的Adaptive Instance时，为{@link ExtensionLoader} 提供信息。
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Adaptive {

    /**
     * 从 {@link URL}的Key名，对应的value作为要Adapt成的Extension名
     * <p>
     *     如果{@link URL}这些Key都没有value,使用缺省的扩展（在接口的SPI中设定的值）。
     *     比如，<code>String[] {"key1","key2"}</code>
     *     表示:
     *     <ol>
     *         <li>现在URL上找Key1的value作为要Adapt成的Extension名</li>
     *         <li>key1没有value,则使用key2的value作为要adapt成的Extension名</li>
     *         <li>key2没有value,则使用缺省的扩展</li>
     *         <li>如果没有设定缺省的扩展，则方法调用会抛出{@link IllegalArgumentException}</li>
     *     </ol>
     *
     * 如果不设置，则使用Extension接口类名的点分隔字串
     * 即对于Extension接口，{@code com.lizanle.dubbo.xxx.YyyInvokerWrapper}的缺省值为<code>String[] {"yyy.invoker.wrapper"}</code>
     *
     * @see SPI#value()
     * @return
     */
    String[] value() default {};
}
