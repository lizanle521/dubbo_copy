package com.lizanle.dubbo.common.copy.extension;

import com.lizanle.dubbo.common.copy.Constants;
import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.utils.ConcurrentHashSet;
import com.lizanle.dubbo.common.copy.utils.Holder;
import com.lizanle.dubbo.common.copy.utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * dubbo使用的扩展点获取
 * <ul>
 *     <li>
 *         自动注入关联扩展点
 *     </li>
 *     <li>
 *         自动wrap上扩展点的wrap类
 *     </li>
 *     <li>
 *         缺省获得的扩展点是一个Adaptive instance.
 *     </li>
 * </ul>
 * @see <a href="http://java.sun.com/j2se/1.5.0/docs/guide/jar/jar.html#Service%20Provider">jdk5.0自动发现机制的实现</a>
 * @see SPI
 * @see Adaptive
 * @see Activate
 * @param <T>
 */
public class ExtensionLoader<T> {
    /**
     * 下边这三个文件夹都可以放扩展点实现类描述文件
     */
    private static final String SERVICES_DIRECTORY = "MET-INF/services/";
    private static final String DUBBO_DIRECTORY = "MET-INF/dubbo/";
    private static final String DUBBO_INTERNAL_DIRECTORY = DUBBO_DIRECTORY+"internal/";

    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");

    private static final ConcurrentHashMap<Class<?>,ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<Class<?>,Object> EXTENSION_INSTANCE = new ConcurrentHashMap<>();
    // ================
    private final Class<?> type;

    private final ExtensionFactory objectFactory;

    private final ConcurrentHashMap<Class<?>,String> cachedNames = new ConcurrentHashMap<>();

    private final Holder<Map<String,Class<?>>> cachedClasses = new Holder();

    private final Map<String,Activate> cachedActivates = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String,Holder<Object>> cachedInstance = new ConcurrentHashMap<>();

    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();

    private volatile Class<?> cachedAdaptiveClass = null;

    private String cachedDefaultName ;

    private volatile Throwable createAdaptiveInstanceError;

    private Set<Class<?>> cachedWrapperClasses;

    private Map<String,IllegalStateException> exceptions = new ConcurrentHashMap<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
        objectFactory = (type == ExtensionFactory.class ? null : ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptvieExtension());
    }

    private static <T> boolean withExtensionAnnotation(Class<T> type){
        return type.isAnnotationPresent(SPI.class);
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        if(type == null){
            throw new IllegalArgumentException("Extension type == null");
        }
        if(!type.isInterface()){
            throw new IllegalArgumentException("Extension type ["+ type + "] is not interface");
        }
        if(!withExtensionAnnotation(type)){
            throw new IllegalArgumentException("Extension type " + type + "is not extension,beacause without "+SPI.class.getSimpleName()+" Annotation");
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>)EXTENSION_LOADERS.get(type);
        if(loader == null ){
            EXTENSION_LOADERS.putIfAbsent(type,new ExtensionLoader<>(type));
            loader = (ExtensionLoader<T>)EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    private static ClassLoader findClassLoader(){
        return ExtensionLoader.class.getClassLoader();
    }

    public String getExtensionName(Class<?> extensionClass){
        return cachedNames.get(extensionClass);
    }

    public String getExtensionName(T extensionInstance){
        return getExtensionName(extensionInstance.getClass());
    }

    /**
     * 获取激活的扩展点
     * @param url
     * @param values
     * @param group
     * @return
     */
    public List<T> getActivateExtension(URL url, String values, String group){
        List<T> exts = new ArrayList<>();
        List<String> names = values == null ? new ArrayList<>(0): Arrays.asList(values);
        // -default 代表去掉默认的扩展
        if(!names.contains(Constants.REMOVE_VALUE_PREFIX + Constants.DEFAULT_KEY)){
            getExtensionClass();
            for (Map.Entry<String, Activate> activateEntry : cachedActivates.entrySet()) {
                String name = activateEntry.getKey();
                Activate activate = activateEntry.getValue();


            }
        }
        return exts;
    }

    /**
     * 返回指定名字的扩展，如果指定的名字的扩展不存在，则抛异常
     * @param name
     * @return
     */
    public T getExtension(String name){
        if(name == null || name.length() == 0){
            throw new IllegalStateException("extension name == null");
        }
        // 名字为true则返回默认扩展,如果默认扩展名也为true,那么就会返回空
        if("true".equals(name)){
            return getDefaultExtension();
        }
        Holder<Object> holder = cachedInstance.get(name);
        if(holder == null){
            cachedInstance.putIfAbsent(name,new Holder<>());
            holder = cachedInstance.get(name);
        }
        Object instance = holder.get();
        if(instance == null){
            synchronized (holder){
                instance = holder.get();
                if(instance == null){
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T)instance;
    }


    private T createExtension(String name){
        Class<?> clazz = getExtensionClass().get(name);
        if(clazz == null){
            throw findException(name);
        }
        try{
            T instance = (T)EXTENSION_INSTANCE.get(clazz);
            if(instance == null){
                EXTENSION_INSTANCE.putIfAbsent(clazz,clazz.newInstance());
                instance = (T)EXTENSION_INSTANCE.get(clazz);
            }
            injectExtension(instance);
            Set<Class<?>> cachedWrapperClasses = this.cachedWrapperClasses;
            if(cachedWrapperClasses != null){
                for (Class<?> wrapperClass : cachedWrapperClasses) {
                    instance = injectExtension((T)wrapperClass.getConstructor(type).newInstance());
                }
            }
            return instance;
        }catch (Throwable e){
            throw new IllegalStateException("extension instance (name:"+name+") (type"+type+") could not be instantiated:"+e.getMessage(),e);
        }
    }

    private T injectExtension(T instance){
        try{
            if(objectFactory != null){
                for (Method method : instance.getClass().getMethods()) {
                    // 找到public的set方法
                    if(method.getName().startsWith("set") &&
                            method.getParameters().length == 1 &&
                            Modifier.isPublic(method.getModifiers())){
                        // 参数类型
                        Class<?> paramType = method.getParameterTypes()[0];
                        try{
                            // 获取 set后边的属性名
                            String propertyName = method.getName().length() > 3 ? method.getName().substring(3,4).toLowerCase()
                                    +method.getName().substring(4) : "";
                            Object extension = objectFactory.getExtension(paramType, propertyName);
                            if(extension != null){
                                method.invoke(instance,extension);
                            }
                        }catch (Exception e){
                            // TODO 记录日志  注入extension失败
                        }
                    }
                }
            }
        }catch (Exception e){

        }
        return instance;
    }

    private IllegalStateException findException(String name){
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if(entry.getKey().toLowerCase().contains(name)){
                return entry.getValue();
            }
        }
        StringBuilder sb = new StringBuilder("no such extension"+type.getName()+"by name"+name);
        int i = 1;
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if(i == 1){
                sb.append(",possible causes:");
            }
            sb.append("\r\n(");
            sb.append(i++);
            sb.append(")");
            sb.append(entry.getKey());
            sb.append(":\r\n");
            sb.append(StringUtils.toString(entry.getValue()));
        }
        return new IllegalStateException(sb.toString());
    }

    /**
     * 返回默认的扩展
     * 没有则返回null
     * @return
     */
    public T getDefaultExtension(){
        getExtensionClass();
        if(null == cachedDefaultName ||  cachedDefaultName.length() == 0
                || "true".equals(cachedDefaultName)){
            return null;
        }
        return getExtension(cachedDefaultName);
    }
    private boolean isMatchGroup(String group,String[] groups){
        // 如果getActiveExtension 的group没有传参数，那么应该就是有active注解就合适
        if(group == null || group.length() == 0){
            return true;
        }
        if(groups != null && groups.length > 0){
            for (String s : groups) {
                if(group.equals(s)){
                    return true;
                }
            }
        }
        // 如果传了group参数但是active没有相应的group参数，那么就是匹配失败
        return false;

    }

    private Map<String,Class<?>> getExtensionClass(){
        Map<String,Class<?>> classes = cachedClasses.get();
        if(classes == null){
            synchronized(cachedClasses){
                classes = cachedClasses.get();
                if(classes == null){
                    classes = loadExtensionClass();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 此方法必须先同步才能调用
     * @return
     */
    private Map<String,Class<?>> loadExtensionClass(){
        final SPI annotation = type.getAnnotation(SPI.class);
        if(annotation != null){
            String value = annotation.value();
            if(value != null && value.trim().length() > 1){
                String[] defaultNames = NAME_SEPARATOR.split(value);
                if(defaultNames.length > 1){
                    throw new IllegalArgumentException("more than 1 default extension name on extension " + type.getName() + ":"+ Arrays.toString(defaultNames));
                }
                if(defaultNames.length == 1) cachedDefaultName = defaultNames[0];
            }
        }

        Map<String,Class<?>> extensionClass = new HashMap<>();
        loadFile(extensionClass,DUBBO_INTERNAL_DIRECTORY);
        loadFile(extensionClass,DUBBO_DIRECTORY);
        loadFile(extensionClass,SERVICES_DIRECTORY);
        return extensionClass;
    }

    private void loadFile(Map<String,Class<?>> extensionClass,String dir){
        String fileName = dir+type.getName();
        try{
            Enumeration<java.net.URL> urls;
            ClassLoader classLoader = findClassLoader();
            if(classLoader != null){
                urls = classLoader.getResources(fileName);
            }else{
                urls = ClassLoader.getSystemResources(fileName);
            }
            if(urls != null){
                while (urls.hasMoreElements()){
                    java.net.URL url = urls.nextElement();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                    try{
                        String line = null;
                        while ((line =reader.readLine()) != null){
                            int commentIndex = line.indexOf("#");
                            if(commentIndex > 0 ) line = line.substring(0,commentIndex);
                            line = line.trim();
                            if(line.length() > 0 ){
                                try{
                                    String name = null;
                                    int i = line.indexOf("=");
                                    if(i > 0){
                                        name = line.substring(0,i).trim();
                                        line = line.substring(i+1).trim();
                                    }
                                    if(line.length() > 0 ){
                                        Class<?> clazz = Class.forName(line,true,classLoader);
                                        if(!type.isAssignableFrom(clazz)){
                                            throw new IllegalStateException("Error when load extenion class (interface:"+ type+",class line"+clazz.getName()+" class:"
                                            +clazz.getName()+"is not subtype of interface");
                                        }

                                        if(clazz.isAnnotationPresent(Adaptive.class)){
                                            if(cachedAdaptiveClass == null){
                                                cachedAdaptiveClass = clazz;
                                            }else if(!cachedAdaptiveClass.equals(clazz)){
                                                // 只能有一个 adaptive class
                                                throw new IllegalStateException("more than 1 adaptive class found:"+cachedAdaptiveClass.getName()+":"+clazz.getName());
                                            }
                                        }else{
                                            try{
                                                clazz.getConstructor(type);
                                                Set<Class<?>> cachedWrapperClasses = this.cachedWrapperClasses;
                                                if(cachedWrapperClasses == null){
                                                    cachedWrapperClasses = new ConcurrentHashSet<Class<?>>();
                                                }
                                                cachedWrapperClasses.add(clazz);
                                            }catch (NoSuchMethodException e){
                                                clazz.getConstructor();
                                                if(name == null || name.length() == 0){
                                                    //name = findAnnotationName(clazz);
                                                    if(name == null || name.length() == 0){
                                                        if(clazz.getSimpleName().length() > type.getSimpleName().length() &&
                                                                clazz.getSimpleName().endsWith(type.getSimpleName())){
                                                            name = clazz.getSimpleName().substring(0,clazz.getSimpleName().length() - type.getSimpleName().length());
                                                        }else{
                                                            throw new IllegalStateException("no such extension "+ clazz.getName()+" in the config" + url);
                                                        }
                                                    }
                                                }
                                                String[] names = NAME_SEPARATOR.split(name);
                                                if(names != null || names.length != 0){
                                                    Activate activate = clazz.getAnnotation(Activate.class);
                                                    if(activate != null){
                                                        cachedActivates.put(names[0],activate);
                                                    }
                                                    for (String n : names) {
                                                        if(!cachedNames.contains(clazz)){
                                                            cachedNames.put(clazz,n);
                                                        }
                                                        Class<?> c = extensionClass.get(n);
                                                        if(c == null){
                                                            extensionClass.put(n,clazz);
                                                        }else if(c == clazz){
                                                            throw new IllegalStateException("duplicat extension"+type.getName()+" name "+n+" on "+c.getName() + " and "+clazz.getName());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }catch (Throwable e){
                                    IllegalStateException exception = new IllegalStateException("Faild to load extension class (interface:" + type + ",class line:" + line + ")in" + url + "cause," + e.getMessage(), e);
                                    exceptions.put(line,exception);
                                }
                            }
                        }// end of while reader lines
                    }catch (Throwable e){

                    }finally {
                        reader.close();
                    }
                }
            }
        }catch (Throwable e){

        }
    }


    public T  getAdaptvieExtension(){
        return null;
    }


}
