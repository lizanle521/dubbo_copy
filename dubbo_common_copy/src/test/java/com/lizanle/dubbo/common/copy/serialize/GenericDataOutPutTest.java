package com.lizanle.dubbo.common.copy.serialize;

import com.lizanle.dubbo.common.copy.serialize.support.dubbo.GenericDataOutput;
import com.lizanle.dubbo.common.copy.utils.ByteUtil;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class GenericDataOutPutTest {
    @Test
    public void testWriteVarint32(){
        int t=1110, v= 1110, ix = 0;
        byte[] b = new byte[1024];
        //把字节由低到高放入字节数组b[1],b[2]
        while (true) {
            b[++ix] = (byte) (v & 0xff);// 取低8位
            System.out.println(ByteUtil.toBit((byte)(v & 0xff)));
            if ((v >>>= 8) == 0) // 已经取的低8位向左移出
                break;
        }

        if (t > 0) {
            // [ 0a e2 => 0a e2 00 ] [ 92 => 92 00 ]
            if (b[ix] < 0) //最高位小于0，则补0
                b[++ix] = 0;
        } else {////是负数，存的是补码（是它相反数的各位取反，末尾加1） 这里做压缩bit位
            // [ 01 ff ff ff => 01 ff ] [ e0 ff ff ff => e0 ]
            while (b[ix] == (byte) 0xff && b[ix - 1] < 0) {// 字节压缩,其实只是读取的时候-1不去读而已，保存还是保存了的，
                ix--;
            }
        }

        b[0] = (byte) (0 + ix - 1);// //存一个标识为，代表有效字节数（0 代表1个字节，1：代表2个字节，2，代表3个字节）
        for (byte b1 : b) {
            System.out.print(b1 + " ");
        }
    }

    @Test
    public void testWriteUInt() {
        byte tmp;
        int v = 255;
        while (true) {
            tmp = (byte) (v & 0x7f);// 取低7位
            if ((v >>>= 7) == 0) { // 如果第8位为0
                System.out.println("high:"+ByteUtil.toBit((byte) (tmp | 0x80))); // 那么第8位补1
                return;
            } else {
                System.out.println("low:"+ByteUtil.toBit(tmp));
            }
        }
    }
}
