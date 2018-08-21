package com.lizanle.dubbo.common.copy.serialize.support.dubbo;

import com.lizanle.dubbo.common.copy.serialize.ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GenericObjectInput extends GenericDataInput implements ObjectInput {

    private static Object SKIPPED_OBJECT = new Object();

    private ClassDescriptorMapper mMapper;

    private List<Object> mRefs = new ArrayList<Object>();

    public GenericObjectInput(InputStream is) {
        this(is, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER);
    }

    public GenericObjectInput(InputStream is, ClassDescriptorMapper mapper) {
        super(is);
        mMapper = mapper;
    }

    public GenericObjectInput(InputStream is, int buffSize) {
        this(is, buffSize, Builder.DEFAULT_CLASS_DESCRIPTOR_MAPPER);
    }

    public GenericObjectInput(InputStream is, int buffSize, ClassDescriptorMapper mapper) {
        super(is, buffSize);
        mMapper = mapper;
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
