package com.lizanle.dubbo.common.copy.serialize;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.Adaptive;
import com.lizanle.dubbo.common.copy.extension.SPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * SPI SIGLETON THREADSAFE
 */
@SPI("hession2")
public interface Serialization {

    /**
     *
     * @return
     */
    byte getContentTypeId();

    /**
     *
     * @return
     */
    String getContentType();

    @Adaptive
    ObjectOutput serialize(URL url, OutputStream outputStream) throws IOException;

    @Adaptive
    ObjectInput deserialize(URL url, InputStream inputStream) throws IOException;
}
