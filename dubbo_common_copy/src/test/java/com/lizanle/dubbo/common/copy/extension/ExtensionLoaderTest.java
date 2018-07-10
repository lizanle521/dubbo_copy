package com.lizanle.dubbo.common.copy.extension;

import com.lizanle.dubbo.common.copy.extension.ext1.SimpleExt;
import com.lizanle.dubbo.common.copy.extension.ext1.impl.SimpleExtImpl1;
import com.lizanle.dubbo.common.copy.extension.ext1.impl.SimpleExtImpl2;
import com.lizanle.dubbo.common.copy.extension.ext2.Ext2;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.WrappedExt;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.impl.Ext5Wrapper1;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.impl.Ext5Wrapper2;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ExtensionLoaderTest {
    private final static Logger logger = LoggerFactory.getLogger(ExtensionLoaderTest.class);
    @Test
    public void extensionLoaderTest_notInterface() {
        try {
            ExtensionLoader<ExtensionLoader> extensionLoader = ExtensionLoader.getExtensionLoader(ExtensionLoader.class);
            fail();
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(),CoreMatchers.containsString("is not interface"));
        }
    }

    @Test
    public void extensionLoaderTest_null_extension(){
        try {
            ExtensionLoader.getExtensionLoader(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), CoreMatchers.containsString("Extension type == null"));
        }
    }

    @Test
    public void extensinoLoaderTest_NoSpiAnnotation() {
        try {
            ExtensionLoader.getExtensionLoader(NoSpi.class);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("is not extension"));
        }
    }

    @Test
    public void getDefaultExtension_test(){
        // 首先确认参数是否正确,是不是接口，是不是有SPI注解
        // 然后看看加载这个类的loader是否存在，没有则创建一个针对这个接口的 loader并缓存起来
        // 然后利用load去加载扩展点，加载扩展点之前访问一下这个loader的扩展点缓存，如果为空就去加载扩展点类
        // 加载扩展点类的时候需要同步
        // 先解析该loader对应的类上边的SPI注解，看看是否有值，有多个值就有问题了。只有一个的话，将这个值存为 cachedDefaultName
        // 然后去三个文件夹里去找 文件名为 loader对应的接口类型名称的文件
        // 找到文件后，用对应的classLoader找到文件对应的URL对象，然后BufferdReader读取URL对象打开的流
        // 逐行读取，将name ,class保存到cachedClassed中缓存起来，以免重复加载
        SimpleExt defaultExtension = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtension();
        assertThat(defaultExtension,instanceOf(SimpleExt.class));

        String name = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtensionName();
        assertEquals(name,"impl1");
        assertThat(name,CoreMatchers.containsString("impl1"));
    }

    @Test
    public void getNoDefaultExtension(){
        Ext2 defaultExtension = ExtensionLoader.getExtensionLoader(Ext2.class).getDefaultExtension();
        assertNull(defaultExtension);

        String name = ExtensionLoader.getExtensionLoader(Ext2.class).getDefaultExtensionName();
        assertNull(name);
    }

    @Test
    public void test_getExtension() throws Exception {
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl1") instanceof SimpleExtImpl1);
        assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl2") instanceof SimpleExtImpl2);
    }

    @Test
    public void test_getExtension_withWrapper() throws Exception {
        WrappedExt impl1 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl1");
        assertThat(impl1,anyOf(instanceOf(Ext5Wrapper1.class),instanceOf(Ext5Wrapper2.class)));

        WrappedExt impl2 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl2");
        assertThat(impl2,anyOf(instanceOf(Ext5Wrapper1.class),instanceOf(Ext5Wrapper2.class)));
    }
}
