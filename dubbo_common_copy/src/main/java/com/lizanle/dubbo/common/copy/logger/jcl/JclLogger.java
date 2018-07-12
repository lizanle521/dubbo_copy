package com.lizanle.dubbo.common.copy.logger.jcl;

import com.lizanle.dubbo.common.copy.logger.Logger;
import org.apache.commons.logging.Log;

import java.io.Serializable;

public class JclLogger implements Logger,Serializable {
    private static final long serialVersionUID = -5351836470565771037L;
    private final Log log;

    public JclLogger(Log log) {
        this.log = log;
    }

    @Override
    public void trace(String msg) {
        log.trace(msg);
    }

    @Override
    public void trace(Throwable e) {
        log.trace(e);
    }

    @Override
    public void trace(String msg, Throwable e) {
        log.trace(msg,e);
    }

    @Override
    public void debug(String msg) {
        log.debug(msg);
    }

    @Override
    public void debug(Throwable e) {
        log.debug(e);
    }

    @Override
    public void debug(String msg, Throwable e) {
        log.debug(msg,e);
    }

    @Override
    public void info(String msg) {
        log.info(msg);
    }

    @Override
    public void info(Throwable e) {
        log.info(e);
    }

    @Override
    public void info(String msg, Throwable e) {
        log.info(msg,e);
    }

    @Override
    public void warn(String msg) {
        log.warn(msg);
    }

    @Override
    public void warn(Throwable e) {
        log.warn(e);
    }

    @Override
    public void warn(String msg, Throwable e) {
        log.warn(msg,e);
    }

    @Override
    public void error(String msg) {
        log.error(msg);
    }

    @Override
    public void error(Throwable e) {
        log.error(e);
    }

    @Override
    public void error(String msg, Throwable e) {
        log.error(msg,e);
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }
}
