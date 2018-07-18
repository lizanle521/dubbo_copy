package com.lizanle.dubbo.common.copy.serialize;

import com.lizanle.dubbo.common.copy.URL;
import com.lizanle.dubbo.common.copy.extension.ext6_wrap.impl.Ext5Wrapper2;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public abstract class AbstractSerializationTest {
    static Random random = new Random();
    Serialization serialization;
    URL url = new URL("protocl", "1.1.1.1", 1234);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Test
    public void test_Bool() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeBool(false);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);


        assertFalse(deserialize.readBool());

        try {
            deserialize.readBool();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_multi_Bool()throws Exception {
        boolean[] array = new boolean[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextBoolean();
        }
        ObjectOutput serialize = serialization.serialize(url, byteArrayOutputStream);
        for (boolean b : array) {
            serialize.writeBool(b);
        }
        serialize.flushBuffer();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, inputStream);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i],deserialize.readBool());
        }

        try {
            deserialize.readBool();
            fail();
        } catch (IOException e) {

        }
    }

    @Test
    public void test_Byte() throws Exception {
        ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
        objectOutput.writeByte((byte)123);
        objectOutput.flushBuffer();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, byteArrayInputStream);


        assertEquals(123,deserialize.readByte());

        try {
            deserialize.readBool();
            fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void test_multi_bytes()throws Exception {
        byte[] array = new byte[100];

        random.nextBytes(array);

        ObjectOutput serialize = serialization.serialize(url, byteArrayOutputStream);
        for (byte b : array) {
            serialize.writeByte(b);
        }
        serialize.flushBuffer();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInput deserialize = serialization.deserialize(url, inputStream);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i],deserialize.readByte());
        }

        try {
            deserialize.readBool();
            fail();
        } catch (IOException e) {

        }
    }
}
