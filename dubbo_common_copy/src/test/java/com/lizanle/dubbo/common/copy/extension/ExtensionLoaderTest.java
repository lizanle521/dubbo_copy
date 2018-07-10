package com.lizanle.dubbo.common.copy.extension;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.autoinjectrelateextension.AutoInjectRelateExtension;
import com.lizanle.dubbo.common.copy.extension.ext1.SimpleExt;
import com.lizanle.dubbo.common.copy.extension.ext1.impl.SimpleExtImpl1;
import com.lizanle.dubbo.common.copy.extension.ext1.impl.SimpleExtImpl2;
import com.lizanle.dubbo.common.copy.extension.ext2.Ext2;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.WrappedExt;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.impl.Ext5Wrapper1;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.impl.Ext5Wrapper2;
import com.lizanle.dubbo.common.copy.extension.ext8_add.AddExt1;
import com.lizanle.dubbo.common.copy.extension.ext8_add.AddExt2;
import com.lizanle.dubbo.common.copy.extension.ext8_add.impl.AddExt1Impl1;
import com.lizanle.dubbo.common.copy.extension.ext8_add.impl.AddExt1_ManualAdaptive;
import com.lizanle.dubbo.common.copy.extension.ext8_add.impl.AddExt1_ManualAdd1;
import com.lizanle.dubbo.common.copy.extension.ext8_add.impl.AddExt2_ManualAdaptive;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

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

    /**
     * 自动注入关联扩展点
     */
    @Test
    public void testAutoInjectRelateExtension() {
        AutoInjectRelateExtension extension = ExtensionLoader.getExtensionLoader(AutoInjectRelateExtension.class).getAdaptvieExtension();
        assertEquals(1,extension.getListSize());
    }

    @Test
    public void test_getExtension_withWrapper() throws Exception {
        WrappedExt impl1 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl1");
        assertThat(impl1,anyOf(instanceOf(Ext5Wrapper1.class),instanceOf(Ext5Wrapper2.class)));

        WrappedExt impl2 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl2");
        assertThat(impl2,anyOf(instanceOf(Ext5Wrapper1.class),instanceOf(Ext5Wrapper2.class)));

        URL url = new URL("p1","1.2.3.4",1010,"path1");
        int count1 = Ext5Wrapper1.echoCount.get();
        int count2 = Ext5Wrapper2.echoCount.get();
        String s = impl1.echo(url, "s");
        assertEquals("ext5imp1-echo",s);
        assertEquals(count1+1,Ext5Wrapper2.echoCount.get());
        assertEquals(count1+1,Ext5Wrapper1.echoCount.get());
    }

    @Test
    public void test_getExtension_noExtension() {
        try {
            SimpleExt xxx = ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("xxx");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("no such extension"));
        }
    }

    @Test
    public void test_getExtension_hasExtension(){
        assertTrue(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1"));
        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1,impl2"));
        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("xxx"));

        try {
            ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension(null);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("extension name == null"));
        }
    }

    @Test
    public void test_getExtension_wrapperIsNotExtension(){
        assertTrue(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1"));
        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1,impl2"));
        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("xxx"));

        assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("wrapper1"));

        try {
            ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension(null);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("extension name == null"));
        }
    }

    @Test
    public void testGetSupportedExtension() {
        Set<String> supportedExtensions = ExtensionLoader.getExtensionLoader(SimpleExt.class).getSupportedExtensions();
        Set<String> set = new HashSet<>();
        set.add("impl1");
        set.add("impl2");
        set.add("impl3");

        assertEquals(supportedExtensions,set);
    }

    @Test
    public void testGetSupportedExtension_wrapperIsNotExt() {
        Set<String> supportedExtensions = ExtensionLoader.getExtensionLoader(WrappedExt.class).getSupportedExtensions();
        Set<String> set = new HashSet<>();
        set.add("impl1");
        set.add("impl2");

        assertEquals(supportedExtensions,set);
    }

    @Test
    public void test_manualAddExtension(){
        try {
            AddExt1 mnual1 = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual1");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("no such extension"));
        }
        ExtensionLoader.getExtensionLoader(AddExt1.class).addExtension("Manual1", AddExt1_ManualAdd1.class);
        AddExt1 mnual1 = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual1");
        assertThat(mnual1,instanceOf(AddExt1_ManualAdd1.class));
        assertEquals("Manual1",ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd1.class));
    }

    @Test
    public void test_AddExtension_whenExistExtension(){
        AddExt1 impl1 = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");

        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).addExtension("impl1", AddExt1Impl1.class);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("already exist"));
        }
    }

    @Test
    public void test_getExtension_adaptive() {
        ExtensionLoader<AddExt2> loader = ExtensionLoader.getExtensionLoader(AddExt2.class);
        loader.addExtension(null, AddExt2_ManualAdaptive.class);

        AddExt2 addExt2 = loader.getAdaptvieExtension();
        assertThat(addExt2,instanceOf(AddExt2_ManualAdaptive.class));
    }

    @Test
    public void test_addAdaptiveExtension_whenExist() {
        ExtensionLoader<AddExt1> loader = ExtensionLoader.getExtensionLoader(AddExt1.class);
        loader.getAdaptvieExtension();

        try {
            loader.addExtension(null, AddExt1_ManualAdaptive.class);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("already exist"));
        }
    }
}
