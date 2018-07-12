package com.lizanle.dubbo.common.copy.logger.jdk;

import com.lizanle.dubbo.common.copy.logger.Level;
import com.lizanle.dubbo.common.copy.logger.Logger;
import com.lizanle.dubbo.common.copy.logger.LoggerAdapter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;

public class JdkLoggerAdapter implements LoggerAdapter {
    private static final String GLOBAL_LOGGER_NAME = "global";

    private File file;

    public JdkLoggerAdapter() {
        // 先加在配置文件
        try{
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties");
            if(in == null){
                System.err.println("no such file loggin.properties for config jdk logger");
            }else{
                LogManager.getLogManager().readConfiguration(in);
            }
        }catch (Throwable e){
            System.err.println("faild to load  loggin.properties for config jdk logger:"+e.getMessage());
        }
        // 反射获取文件
        try {
            Handler[] handlers = java.util.logging.Logger.getLogger(GLOBAL_LOGGER_NAME).getHandlers();
            if(handlers != null){
                for (Handler handler : handlers) {
                    if(handler instanceof FileHandler){
                        FileHandler fileHandler = (FileHandler) handler;
                        Field files = fileHandler.getClass().getField("files");
                        File[] fileArr = (File[])files.get(fileHandler);
                        if(fileArr != null && fileArr.length != 0){
                            file = fileArr[0];
                        }
                    }
                }
            }
        } catch (Throwable e) {

        }
    }

    public static Level fromJdkLevel(java.util.logging.Level level){
        if(level == java.util.logging.Level.ALL){
            return Level.ALL;
        }else if(level == java.util.logging.Level.FINER){
            return Level.TRACE;
        }else if(level == java.util.logging.Level.FINE){
            return Level.DEBUG;
        }else if(level == java.util.logging.Level.INFO){
            return Level.INFO;
        }else if(level == java.util.logging.Level.WARNING){
            return Level.WARN;
        }else if(level == java.util.logging.Level.SEVERE){
            return Level.ERROR;
        }
        return Level.OFF;
    }

    public static java.util.logging.Level toJdkLevel(Level level){
        if(level == Level.ALL){
            return java.util.logging.Level.ALL;
        }else if(level == Level.TRACE){
            return java.util.logging.Level.FINER;
        }else if(level == Level.DEBUG){
            return java.util.logging.Level.FINE;
        }else if(level == Level.INFO){
            return java.util.logging.Level.INFO;
        }else if(level == Level.WARN){
            return java.util.logging.Level.WARNING;
        }else if(level == Level.ERROR){
            return java.util.logging.Level.SEVERE;
        }
        return java.util.logging.Level.OFF;
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new JdkLogger(java.util.logging.Logger.getLogger(key == null ? "" : key.getName()));
    }

    @Override
    public Logger getLogger(String key) {
        return new JdkLogger(java.util.logging.Logger.getLogger(key == null ? "" : key));
    }

    @Override
    public Level getLevel() {
        return fromJdkLevel(java.util.logging.Logger.getLogger(GLOBAL_LOGGER_NAME).getLevel());
    }

    @Override
    public void setLevel(Level level) {
        java.util.logging.Logger.getLogger(GLOBAL_LOGGER_NAME).setLevel(toJdkLevel(level));
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {

    }
}
