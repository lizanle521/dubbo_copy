package com.lizanle.dubbo.common.copy.logger;

import java.io.File;

/**
 * 实际的Logger提供者
 * @author lizanle
 */
public interface LoggerAdapter {
    /**
     * 获取日志输出器
     * @param key 分类键
     * @return 日志输出器，不返回null
     */
    Logger getLogger(Class<?> key);

    /**
     * 获取日志输出器
     * @param key 分类键
     * @return 日志输出器，不返回null
     */
    Logger getLogger(String key);

    /**
     * 获取日志输出登记
     * @return 日志输出级别
     */
    Level getLevel();

    /**
     * 设置日志输出级别
     * @param level 日志输出级别
     */
    void setLevel(Level level);

    /**
     * 获取当前日志文件
     * @return
     */
    File getFile();

    /**
     *
     */
    void setFile(File file);
}
