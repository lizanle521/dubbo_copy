package com.lizanle.dubbo.common.copy.compiler.support;

import com.lizanle.dubbo.common.copy.compiler.Compiler;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;

@Adaptive
public class AdaptiveCompiler implements Compiler {
    private static volatile String DEFAULT_COMPILER  ;

    public static void setDefaultCompiler(String compiler){
        DEFAULT_COMPILER = compiler;
    }
    @Override
    public Class<?> compile(String code, ClassLoader classLoader) {
        Compiler compiler;
        ExtensionLoader<Compiler> extensionLoader = ExtensionLoader.getExtensionLoader(Compiler.class);
        String name = DEFAULT_COMPILER;
        if(name != null && name.length() > 0){
            compiler = extensionLoader.getExtension(name);
        }else{
            compiler = extensionLoader.getDefaultExtension();
        }
        return compiler.compile(code,classLoader);
    }
}
