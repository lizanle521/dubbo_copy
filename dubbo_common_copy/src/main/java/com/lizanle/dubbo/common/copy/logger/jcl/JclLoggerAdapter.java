package com.lizanle.dubbo.common.copy.logger.jcl;

import com.lizanle.dubbo.common.copy.logger.Level;
import com.lizanle.dubbo.common.copy.logger.Logger;
import com.lizanle.dubbo.common.copy.logger.LoggerAdapter;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class JclLoggerAdapter implements LoggerAdapter {
    private File file;
    private Level level;
    @Override
    public Logger getLogger(Class<?> key) {
        return new JclLogger(LogFactory.getLog(key));
    }

    @Override
    public Logger getLogger(String key) {
        return new JclLogger(LogFactory.getLog(key));
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }
}
