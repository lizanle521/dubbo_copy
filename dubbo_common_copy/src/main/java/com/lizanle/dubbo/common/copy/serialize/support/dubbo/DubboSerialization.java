package com.lizanle.dubbo.common.copy.serialize.support.dubbo;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.serialize.ObjectInput;
import com.lizanle.dubbo.common.copy.serialize.ObjectOutput;
import com.lizanle.dubbo.common.copy.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * dubbo序列化
 */
public class DubboSerialization implements Serialization {
    @Override
    public byte getContentTypeId() {
        return 1;
    }

    @Override
    public String getContentType() {
        return "x/application-dubbo";
    }

    @Override
    public ObjectOutput serialize(URL url, OutputStream outputStream) throws IOException {
        return new GenericObjectOutput(outputStream);
    }

    @Override
    public ObjectInput deserialize(URL url, InputStream inputStream) throws IOException {
        return new GenericObjectInput(inputStream);
    }
}
