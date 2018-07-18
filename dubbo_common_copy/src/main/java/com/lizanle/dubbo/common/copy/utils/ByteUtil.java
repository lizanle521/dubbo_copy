package com.lizanle.dubbo.common.copy.utils;

/**
 * 字节转换工具类
 */
public class ByteUtil {
    public static String toBit(byte b){
        return "" + (byte)((b >> 7) & 0x1) + (byte)((b >> 6) & 0x1) +
                (byte)((b >> 5) & 0x1) + (byte)((b >> 4) & 0x1) +
                (byte)((b >> 3) & 0x1) + (byte)((b >> 2) & 0x1) +
                (byte)((b >> 1) & 0x1) + (byte)((b >> 0) & 0x1) ;
    }
}
