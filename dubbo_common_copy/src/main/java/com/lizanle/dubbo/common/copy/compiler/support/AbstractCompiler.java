package com.lizanle.dubbo.common.copy.compiler.support;

import com.lizanle.dubbo.common.copy.compiler.Compiler;
import com.lizanle.dubbo.common.copy.utils.ClassHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractCompiler implements Compiler {
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");

    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

    @Override
    public Class<?> compile(String code, ClassLoader classLoader) {
        code = code.trim();
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        String pkg = null;
        if(matcher.find()){
            pkg = matcher.group(1);
        }else{
            pkg = "";
        }
        matcher = CLASS_PATTERN.matcher(code);
        String cls;
        if(matcher.find()){
            cls = matcher.group(1);
        }else{
            throw new IllegalArgumentException("No such class name in " + code);
        }
        String className = pkg != null && pkg.length() > 0 ? pkg + "." + cls : cls;
        try{
            return Class.forName(className,true, ClassHelper.getCallerClassLoder(getClass()));
        }catch (Throwable e){
            try{
                return doCompile(className,code);
            }catch (RuntimeException t){
                throw t;
            }catch (Throwable t){
                throw new IllegalStateException("Faild to compile class,cause:" + t.getMessage() +
                        ",class:" + className+ ",code:" + code+"\n,stack:" + ClassUtils.toString(t));
            }
        }
    }

    protected abstract Class<?> doCompile(String name,String source) throws Throwable;
}
