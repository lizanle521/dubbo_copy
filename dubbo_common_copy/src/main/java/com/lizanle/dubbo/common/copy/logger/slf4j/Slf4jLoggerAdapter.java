package com.lizanle.dubbo.common.copy.logger.slf4j;

import com.lizanle.dubbo.common.copy.logger.Level;
import com.lizanle.dubbo.common.copy.logger.Logger;
import com.lizanle.dubbo.common.copy.logger.LoggerAdapter;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Slf4jLoggerAdapter implements LoggerAdapter {
    private File file;
    private Level level;
    @Override
    public Logger getLogger(Class<?> key) {
        return new Slf4jLogger(LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(String key) {
        return new Slf4jLogger(LoggerFactory.getLogger(key));
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
