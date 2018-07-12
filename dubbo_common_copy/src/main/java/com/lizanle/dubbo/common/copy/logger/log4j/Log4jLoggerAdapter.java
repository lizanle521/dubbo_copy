package com.lizanle.dubbo.common.copy.logger.log4j;

import com.lizanle.dubbo.common.copy.logger.Level;
import com.lizanle.dubbo.common.copy.logger.LoggerAdapter;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Enumeration;

public class Log4jLoggerAdapter implements LoggerAdapter {
    private File file;

    public Log4jLoggerAdapter() {
        try{
            Logger rootLogger = LogManager.getRootLogger();
            if(rootLogger != null){
                Enumeration<Appender> allAppenders = rootLogger.getAllAppenders();
                if(allAppenders != null) {
                    while (allAppenders.hasMoreElements()) {
                        Appender appender = allAppenders.nextElement();
                        if (appender instanceof FileAppender) {
                            FileAppender fileAppender = (FileAppender) appender;
                            String fileName = fileAppender.getFile();
                            this.file = new File(fileName);
                            break;
                        }
                    }
                }
            }
        }catch (Throwable e){

        }
    }



    @Override
    public com.lizanle.dubbo.common.copy.logger.Logger getLogger(Class<?> key) {
        return new Log4jLogger(LogManager.getLogger(key));
    }

    @Override
    public com.lizanle.dubbo.common.copy.logger.Logger getLogger(String key) {
        return new Log4jLogger(LogManager.getLogger(key));
    }

    private static org.apache.log4j.Level toLog4jLevel(Level level) {
        if (level == Level.ALL)
            return org.apache.log4j.Level.ALL;
        if (level == Level.TRACE)
            return org.apache.log4j.Level.TRACE;
        if (level == Level.DEBUG)
            return org.apache.log4j.Level.DEBUG;
        if (level == Level.INFO)
            return org.apache.log4j.Level.INFO;
        if (level == Level.WARN)
            return org.apache.log4j.Level.WARN;
        if (level == Level.ERROR)
            return org.apache.log4j.Level.ERROR;
        // if (level == Level.OFF)
        return org.apache.log4j.Level.OFF;
    }

    private static Level fromLog4jLevel(org.apache.log4j.Level level) {
        if (level == org.apache.log4j.Level.ALL)
            return Level.ALL;
        if (level == org.apache.log4j.Level.TRACE)
            return Level.TRACE;
        if (level == org.apache.log4j.Level.DEBUG)
            return Level.DEBUG;
        if (level == org.apache.log4j.Level.INFO)
            return Level.INFO;
        if (level == org.apache.log4j.Level.WARN)
            return Level.WARN;
        if (level == org.apache.log4j.Level.ERROR)
            return Level.ERROR;
        // if (level == org.apache.log4j.Level.OFF)
        return Level.OFF;
    }


    @Override
    public Level getLevel() {
        return fromLog4jLevel(LogManager.getRootLogger().getLevel());
    }

    @Override
    public void setLevel(Level level) {
        LogManager.getRootLogger().setLevel(toLog4jLevel(level));
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {

    }
}
