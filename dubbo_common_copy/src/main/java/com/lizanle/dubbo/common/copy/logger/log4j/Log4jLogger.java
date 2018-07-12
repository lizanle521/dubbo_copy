package com.lizanle.dubbo.common.copy.logger.log4j;

import com.lizanle.dubbo.common.copy.logger.Logger;
import com.lizanle.dubbo.common.copy.logger.support.FailSafeLogger;
import org.apache.log4j.Level;

public class Log4jLogger implements Logger{

    private final static String FQCN = FailSafeLogger.class.getName();

    private org.apache.log4j.Logger logger;

    public Log4jLogger(org.apache.log4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void trace(String msg) {
        logger.log(FQCN, Level.TRACE,msg,null);
    }

    @Override
    public void trace(Throwable e) {
        logger.log(FQCN,Level.TRACE,e.getMessage(),e);
    }

    @Override
    public void trace(String msg, Throwable e) {
        logger.log(FQCN,Level.TRACE,msg,e);
    }

    @Override
    public void debug(String msg) {
        logger.log(FQCN,Level.DEBUG,msg,null);
    }

    @Override
    public void debug(Throwable e) {
        logger.log(FQCN,Level.DEBUG,e.getMessage(),e);
    }

    @Override
    public void debug(String msg, Throwable e) {
        logger.log(FQCN,Level.DEBUG,msg,e);
    }

    @Override
    public void info(String msg) {
        logger.log(FQCN,Level.INFO,msg,null);
    }

    @Override
    public void info(Throwable e) {
        logger.log(FQCN,Level.INFO,e.getMessage(),e);
    }

    @Override
    public void info(String msg, Throwable e) {
        logger.log(FQCN,Level.INFO,msg,e);
    }

    @Override
    public void warn(String msg) {
        logger.log(FQCN,Level.WARN,msg,null);
    }

    @Override
    public void warn(Throwable e) {
        logger.log(FQCN,Level.WARN,e.getMessage(),e);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.log(FQCN,Level.WARN,msg,e);
    }

    @Override
    public void error(String msg) {
        logger.log(FQCN,Level.ERROR,msg,null);
    }

    @Override
    public void error(Throwable e) {
        logger.log(FQCN,Level.ERROR,e.getMessage(),e);
    }

    @Override
    public void error(String msg, Throwable e) {
        logger.log(FQCN,Level.ERROR,msg,e);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }
}
