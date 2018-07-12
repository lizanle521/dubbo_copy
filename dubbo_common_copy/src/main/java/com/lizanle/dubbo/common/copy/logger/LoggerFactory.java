package com.lizanle.dubbo.common.copy.logger;

import com.lizanle.dubbo.common.copy.extension.ExtensionLoader;
import com.lizanle.dubbo.common.copy.logger.jcl.JclLoggerAdapter;
import com.lizanle.dubbo.common.copy.logger.jdk.JdkLoggerAdapter;
import com.lizanle.dubbo.common.copy.logger.log4j.Log4jLoggerAdapter;
import com.lizanle.dubbo.common.copy.logger.slf4j.Slf4jLoggerAdapter;
import com.lizanle.dubbo.common.copy.logger.support.FailSafeLogger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志输出工厂
 */
public class LoggerFactory {
    private final static ConcurrentHashMap<String,FailSafeLogger> LOGGERS = new ConcurrentHashMap<>();

    private static volatile LoggerAdapter loggerAdapter;

    static {
        String logger = System.getProperty("dubbo.application.logger");
        if("jcl".equals(logger)){
            setLoggerAdapter(new JclLoggerAdapter());
        }else if("jdk".equals(logger)){
            setLoggerAdapter(new JdkLoggerAdapter());
        }else if("slf4j".equals(logger)){
            setLoggerAdapter(new Slf4jLoggerAdapter());
        }else if("log4j".equals(logger)){
            setLoggerAdapter(new Log4jLoggerAdapter());
        }else{
            try {
                setLoggerAdapter(new Log4jLoggerAdapter());
            } catch (Throwable e1) {
                try {
                    setLoggerAdapter(new Slf4jLoggerAdapter());
                } catch (Throwable e2) {
                    try {
                        setLoggerAdapter(new JclLoggerAdapter());
                    } catch (Throwable e3) {
                        setLoggerAdapter(new JdkLoggerAdapter());
                    }
                }
            }
        }
    }

    private LoggerFactory() {
    }

    public static void setLoggerAdapter(String loggerAdapter){
        setLoggerAdapter(ExtensionLoader.getExtensionLoader(LoggerAdapter.class).getExtension(loggerAdapter));
    }

    public static void setLoggerAdapter(LoggerAdapter loggerAdapter) {
        if(loggerAdapter != null) {
            Logger logger = loggerAdapter.getLogger(LoggerFactory.class.getName());
            logger.info("use logger :" + loggerAdapter.getClass().getName());
            LoggerFactory.loggerAdapter = loggerAdapter;
            for (Map.Entry<String, FailSafeLogger> entry : LOGGERS.entrySet()) {
                entry.getValue().setLogger(loggerAdapter.getLogger(entry.getKey()));
            }
        }
    }

    public static Logger getLogger(Class<?> key){
        FailSafeLogger logger = LOGGERS.get(key.getName());
        if(logger == null){
            LOGGERS.putIfAbsent(key.getName(),new FailSafeLogger(loggerAdapter.getLogger(key)));
            logger = LOGGERS.get(key.getName());
        }
        return logger;
    }

    public static Logger getLogger(String key){
        FailSafeLogger logger = LOGGERS.get(key);
        if(logger == null){
            LOGGERS.putIfAbsent(key,new FailSafeLogger(loggerAdapter.getLogger(key)));
            logger = LOGGERS.get(key);
        }
        return logger;
    }

    public static Level getLevel(){
        return loggerAdapter.getLevel();
    }

    public static void setLevel(Level level){
        loggerAdapter.setLevel(level);
    }

    public static File getFile(){
        return loggerAdapter.getFile();
    }
}
