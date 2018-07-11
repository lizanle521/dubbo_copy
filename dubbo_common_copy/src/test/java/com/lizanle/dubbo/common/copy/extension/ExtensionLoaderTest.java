package com.lizanle.dubbo.common.copy.extension;

import com.lizanle.dubbo.common.copy.Constants;
import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.activate.ActivateExt1;
import com.lizanle.dubbo.common.copy.extension.activate.impl.*;
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
import com.lizanle.dubbo.common.copy.extension.ext8_add.impl.*;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
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
        // AddExt1 的echo方法 有Adaptive注解，就会以编码的方式（前提是没有Adaptive注解的接口实现类，并且参数中带有URL对象）生成一个Adaptive类，
        // 默认会去获取Adaptive方法中的URL参数，然后通过扩展点的名称去获取url中的参数传值,譬如AddExt1的SPI默认值为impl,且Adaptive注解没有值，那么
        // 获取url参数的方式就是 extName = url.getParameter("add.ext1","impl1")
        // 如果adaptive注解有值 "key1","key2"，那么就会用adaptive注解的值做key去url中找扩展点名字,
        // 如果spi没有默认值，而key1 key2参数又没传的话，那么extName就会为空，就会报扩展点名字为空 extension name == null
        // 这个extName就是Adaptive类 通过ExtensionLoader类去获取的扩展点的名字

        // 这里Adaptive的实现思路就是采用简单代理模式，用生成的类或者已经存在的代理类 调用实际存在的实现类的方法，
        loader.getAdaptvieExtension();
        try {
            loader.addExtension(null, AddExt1_ManualAdaptive.class);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("already exist"));
        }
    }

    @Test
    public void test_getExtension_replaceExtension(){
        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual2");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),CoreMatchers.containsString("no such extension"));
        }

        {
            AddExt1 impl1 = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");
            assertThat(impl1,instanceOf(AddExt1Impl1.class));
            assertEquals("impl1",ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1Impl1.class));
        }

        {
            ExtensionLoader.getExtensionLoader(AddExt1.class).replaceExtension("impl1", AddExt1_ManualAdd2.class);
            AddExt1 impl1 = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");
            assertThat(impl1,instanceOf(AddExt1_ManualAdd2.class));
            assertEquals("impl1",ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd2.class));
        }
    }

    @Test
    public void loadActivateExtension(){
        URL url = URL.valueOf("test://localhost/test");
        // 通过group 筛选，group符合要求 且 url中存在 activae value中的key(如果activate value没有值，则默认匹配)
        List<ActivateExt1> list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{}, "default_group");
        assertEquals(1,list.size());
        assertTrue(list.get(0).getClass() == ActivateExt1Impl1.class);

        // test group
        url = url.addParameter(Constants.GROUP_KEY,"group1");
        List<ActivateExt1> list1 = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{}, "group1");
        assertEquals(1,list1.size());
        assertTrue(list1.get(0).getClass() == GroupActivateExtImpl1.class);

        // test value
        url =  url.removeParameter(Constants.GROUP_KEY);
        url = url.addParameter(Constants.GROUP_KEY,"value");
        url = url.addParameter("value","value");
        List<ActivateExt1> list2 = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{}, "value");
        assertEquals(1,list2.size());
        assertTrue(list2.get(0).getClass() == ValueActivateExtImpl1.class);

        // test order
        url = URL.valueOf("test://localhost/test");
        url.addParameter(Constants.GROUP_KEY,"order");

        List<ActivateExt1> list3 = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String(), "order");
        assertEquals(2,list3.size());
        assertTrue(list3.get(0).getClass() == OrderActivateExtImpl1.class);
        assertTrue(list3.get(1).getClass() == OrderActivateExtImpl2.class);
    }

    @Test
    public void testLoadDefaultActivateExtension() throws Exception {
        // test default
        URL url = URL.valueOf("test://localhost/test?ext=order1,default");
        List<ActivateExt1> list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, "ext", "default_group");
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).getClass() == OrderActivateExtImpl1.class);
        Assert.assertTrue(list.get(1).getClass() == ActivateExt1Impl1.class);

        url = URL.valueOf("test://localhost/test?ext=default,order1");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class)
                .getActivateExtension(url, "ext", "default_group");
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).getClass() == ActivateExt1Impl1.class);
        Assert.assertTrue(list.get(1).getClass() == OrderActivateExtImpl1.class);
    }


}
