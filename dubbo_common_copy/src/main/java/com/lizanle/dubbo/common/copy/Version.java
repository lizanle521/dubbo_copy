package com.lizanle.dubbo.common.copy;

import com.lizanle.dubbo.common.copy.logger.Logger;
import com.lizanle.dubbo.common.copy.logger.LoggerFactory;

public class Version {
    private static final Logger logger = LoggerFactory.getLogger(Version.class);
    private static final String VERSION = "2.0.0";

    private Version() {
    }

    public static String getVersion() {
        return VERSION;
    }
}
