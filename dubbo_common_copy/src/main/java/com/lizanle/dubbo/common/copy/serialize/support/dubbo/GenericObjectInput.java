package com.lizanle.dubbo.common.copy.serialize.support.dubbo;

import com.lizanle.dubbo.common.copy.serialize.DataInput;
import com.lizanle.dubbo.common.copy.serialize.ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class GenericObjectInput extends GenericDataInput implements ObjectInput {
    public GenericObjectInput(InputStream mInput) {
        super(mInput);
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        return null;
    }
}
