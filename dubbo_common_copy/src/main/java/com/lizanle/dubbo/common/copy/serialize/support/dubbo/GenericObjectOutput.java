package com.lizanle.dubbo.common.copy.serialize.support.dubbo;

import com.lizanle.dubbo.common.copy.serialize.ObjectOutput;

import java.io.IOException;
import java.io.OutputStream;

public class GenericObjectOutput extends GenericDataOutput implements ObjectOutput {
    public GenericObjectOutput(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    public void writeObject(Object obj) throws IOException {

    }
}
