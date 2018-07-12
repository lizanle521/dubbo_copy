package com.lizanle.dubbo.common.copy.logger.support;

import com.lizanle.dubbo.common.copy.Version;
import com.lizanle.dubbo.common.copy.logger.Logger;
import com.lizanle.dubbo.common.copy.utils.NetUtils;

public class FailSafeLogger implements Logger {
    private Logger logger;

    public FailSafeLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void trace(String msg) {
        try {
            logger.trace(msg);
        } catch (Exception e) {

        }
    }

    private String appendContextMessage(String msg){
        return "DUBBO: " + msg + " VERSION:" + Version.getVersion()+ " Current Host:" + NetUtils.getLogHost();
    }

    @Override
    public void trace(Throwable e) {
        try {
            logger.trace(e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void trace(String msg, Throwable e) {
        try {
            logger.trace(appendContextMessage(msg),e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void debug(String msg) {
        try {
            logger.debug(appendContextMessage(msg));
        } catch (Exception e) {

        }
    }

    @Override
    public void debug(Throwable e) {
        try {
            logger.debug(e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void debug(String msg, Throwable e) {
        try {
            logger.debug(appendContextMessage(msg),e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void info(String msg) {
        try {
            logger.info(appendContextMessage(msg));
        } catch (Exception e) {

        }
    }

    @Override
    public void info(Throwable e) {
        try {
            logger.info(e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void info(String msg, Throwable e) {
        try {
            logger.info(appendContextMessage(msg),e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void warn(String msg) {
        try {
            logger.warn(appendContextMessage(msg));
        } catch (Exception e) {

        }
    }

    @Override
    public void warn(Throwable e) {
        try {
            logger.warn(e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void warn(String msg, Throwable e) {
        try {
            logger.warn(appendContextMessage(msg),e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void error(String msg) {
        try {
            logger.error(appendContextMessage(msg));
        } catch (Exception e) {

        }
    }

    @Override
    public void error(Throwable e) {
        try {
            logger.error(e);
        } catch (Exception e1) {

        }
    }

    @Override
    public void error(String msg, Throwable e) {
        try {
            logger.error(appendContextMessage(msg),e);
        } catch (Exception e1) {

        }
    }

    @Override
    public boolean isTraceEnabled() {
        try {
            return logger.isTraceEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isDebugEnabled() {
        try {
            return logger.isDebugEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInfoEnabled() {
        try {
            return logger.isInfoEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isWarnEnabled() {
        try {
            return logger.isWarnEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isErrorEnabled() {
        try {
            return logger.isErrorEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
