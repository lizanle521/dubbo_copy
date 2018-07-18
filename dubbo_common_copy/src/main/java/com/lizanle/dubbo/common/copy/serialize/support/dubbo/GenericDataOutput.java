package com.lizanle.dubbo.common.copy.serialize.support.dubbo;

import com.lizanle.dubbo.common.copy.serialize.DataOutput;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 原生类型写入
 */
public class GenericDataOutput implements DataOutput,GenericDataFlags {
    private static final int CHAR_BUFF_SIZE = 256;

    /**
     * 缓冲数组
     */
    private final byte[] mBuffer,mTemp = new byte[9];

    /**
     * 字节缓冲数组
     */
    private final char[] mCharBuffer = new char[CHAR_BUFF_SIZE];

    /**
     * 输出流
     */
    private final OutputStream outputStream;

    /**
     * 写入缓冲的限制，等于缓冲数组的长度
     */
    private final int mLimit ;

    /**
     * 写入缓冲数组的未知
     */
    private int mPosition = 0;

    public GenericDataOutput(OutputStream outputStream) {
        this(outputStream,1024);
    }

    public GenericDataOutput(OutputStream outputStream,int bufferSize) {
        mLimit = bufferSize;
        mBuffer = new byte[bufferSize];
        this.outputStream = outputStream;
    }

    /**
     * 这里用指定的VARINT_0 VARINT_1 代表 bool值，读取的时候也保持一致就可以了
     * @param v value.
     * @throws IOException
     */
    @Override
    public void writeBool(boolean v) throws IOException {
        write0(v ? VARINT_1 : VARINT_0);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        switch (v) {
            case 0:
                write0(VARINT_0);
                break;
            case 1:
                write0(VARINT_1);
                break;
            case 2:
                write0(VARINT_2);
                break;
            case 3:
                write0(VARINT_3);
                break;
            case 4:
                write0(VARINT_4);
                break;
            case 5:
                write0(VARINT_5);
                break;
            case 6:
                write0(VARINT_6);
                break;
            case 7:
                write0(VARINT_7);
                break;
            case 8:
                write0(VARINT_8);
                break;
            case 9:
                write0(VARINT_9);
                break;
            case 10:
                write0(VARINT_A);
                break;
            case 11:
                write0(VARINT_B);
                break;
            case 12:
                write0(VARINT_C);
                break;
            case 13:
                write0(VARINT_D);
                break;
            case 14:
                write0(VARINT_E);
                break;
            case 15:
                write0(VARINT_F);
                break;
            case 16:
                write0(VARINT_10);
                break;
            case 17:
                write0(VARINT_11);
                break;
            case 18:
                write0(VARINT_12);
                break;
            case 19:
                write0(VARINT_13);
                break;
            case 20:
                write0(VARINT_14);
                break;
            case 21:
                write0(VARINT_15);
                break;
            case 22:
                write0(VARINT_16);
                break;
            case 23:
                write0(VARINT_17);
                break;
            case 24:
                write0(VARINT_18);
                break;
            case 25:
                write0(VARINT_19);
                break;
            case 26:
                write0(VARINT_1A);
                break;
            case 27:
                write0(VARINT_1B);
                break;
            case 28:
                write0(VARINT_1C);
                break;
            case 29:
                write0(VARINT_1D);
                break;
            case 30:
                write0(VARINT_1E);
                break;
            case 31:
                write0(VARINT_1F);
                break;
            default:
                write0(VARINT8);
                write0(v);
        }
    }

    @Override
    public void writeShort(short v) throws IOException {
        writeVarint32(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        writeVarint32(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        writeVarint64(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeVarint32(Float.floatToRawIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeVarint64(Double.doubleToRawLongBits(v));
    }

    /*** 处理字符串是，取每个字符
     *   循环处理，每次最多处理256个字符，
     *   具体，把字符通过String的getChars方法放入字符数组，再把字符转为字节，同时做utf8编码
     *   具体可以看看，utf8编码规则，要不然不好懂。
     *   对字符串的序列化由于用了utf8编码，相对unicode其实是放大了存储空间
     */
    @Override
    public void writeUTF(String v) throws IOException {
        if (v == null) {
            write0(OBJECT_NULL);
        } else {
            int len = v.length();
            if (len == 0) {
                write0(OBJECT_DUMMY);
            } else {
                write0(OBJECT_BYTES);
                writeUInt(len);

                int off = 0, limit = mLimit - 3, size;
                char[] buf = mCharBuffer;
                do { // utf-8编码
                    //最大256
                    size = Math.min(len - off, CHAR_BUFF_SIZE);
                    // 把char字符放入buf
                    v.getChars(off, off + size, buf, 0);

                    for (int i = 0; i < size; i++) {
                        char c = buf[i];
                        if (mPosition > limit) {//还剩2字节缓冲区
                            if (c < 0x80) { //如果一字节能表示，就用一字节 1000,0000
                                write0((byte) c);
                            } else if (c < 0x800) { //0000 1000,0000,0000
                                write0((byte) (0xC0 | ((c >> 6) & 0x1F)));//取高5位，前面补110
                                write0((byte) (0x80 | (c & 0x3F))); //取低六位前面补10
                            } else {
                                write0((byte) (0xE0 | ((c >> 12) & 0x0F))); //取高4位，前面补1110
                                write0((byte) (0x80 | ((c >> 6) & 0x3F)));//取中6位，前面补10
                                write0((byte) (0x80 | (c & 0x3F)));//取末尾6位，前面补10
                            }
                        } else {//还剩3个以上字节缓冲区，直接放缓冲区
                            if (c < 0x80) {
                                mBuffer[mPosition++] = (byte) c;
                            } else if (c < 0x800) {
                                mBuffer[mPosition++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                                mBuffer[mPosition++] = (byte) (0x80 | (c & 0x3F));
                            } else {
                                mBuffer[mPosition++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                                mBuffer[mPosition++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                                mBuffer[mPosition++] = (byte) (0x80 | (c & 0x3F));
                            }
                        }
                    }
                    off += size;//取下一字节段
                }
                while (off < len);
            }
        }
    }

    @Override
    public void writeBytes(byte[] v) throws IOException {
        if(v == null){
            write0(OBJECT_NULL);
        }else{
            writeBytes(v,0,v.length);
        }
    }

    @Override
    public void writeBytes(byte[] v, int off, int len) throws IOException {
        if(len == 0){
            write0(OBJECT_DUMMY);
        }else{
            write0(OBJECT_BYTES);
            writeUInt(len);
            write0(v,off,len);
        }
    }

    public void writeUInt(int v) throws IOException {
        byte tmp;
        while (true) {
            tmp = (byte) (v & 0x7f);// 取低7位
            if ((v >>>= 7) == 0) { // 如果是最高位为0
                write0((byte) (tmp | 0x80)); // 那么最高位补1
                return;
            } else {
                write0(tmp);
            }
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        if(mPosition > 0){
            outputStream.write(mBuffer,0,mPosition);
            mPosition = 0;
        }
    }

    protected void write0(byte b) throws IOException{
        // 长度达到最大限制，则buffer写满了，就往输出流里边写一次
        if(mPosition == mLimit)
            flushBuffer();
        mBuffer[mPosition++] = b;
    }

    protected void write0(byte[] b, int off, int len) throws IOException {
        // 剩余的空间
        int rem = mLimit - mPosition;
        // 如果剩余空间大于要写入的长度，直接copy
        if (rem > len) {
            System.arraycopy(b, off, mBuffer, mPosition, len);
            mPosition += len;
        } else {
            // 否则先copy剩余的空间
            System.arraycopy(b, off, mBuffer, mPosition, rem);
            mPosition = mLimit;
            // 然后buffer写入流
            flushBuffer();
            // 位置偏移加上copy之前剩余的空间长度
            off += rem;
            // 需要copy的长度减去已经copy的长度
            len -= rem;
            // 如果需要copy的长度小于buffer的长度，
            // 将 b中的剩余的数据copy到buffer中
            if (mLimit > len) {
                System.arraycopy(b, off, mBuffer, 0, len);
                mPosition = len;
            } else {
                // 如果b中剩余的长度还是大于buffer的长度，那么直接将b中剩下的数据写入到流
                outputStream.write(b, off, len);
            }
        }
    }

    private void writeVarint32(int v) throws IOException{
        switch (v){
            case -15:
                write0(VARINT_NF);
                break;
            case -14:
                write0(VARINT_NE);
                break;
            case -13:
                write0(VARINT_ND);
                break;
            case -12:
                write0(VARINT_NC);
                break;
            case -11:
                write0(VARINT_NB);
                break;
            case -10:
                write0(VARINT_NA);
                break;
            case -9:
                write0(VARINT_N9);
                break;
            case -8:
                write0(VARINT_N8);
                break;
            case -7:
                write0(VARINT_N7);
                break;
            case -6:
                write0(VARINT_N6);
                break;
            case -5:
                write0(VARINT_N5);
                break;
            case -4:
                write0(VARINT_N4);
                break;
            case -3:
                write0(VARINT_N3);
                break;
            case -2:
                write0(VARINT_N2);
                break;
            case -1:
                write0(VARINT_N1);
                break;
            case 0:
                write0(VARINT_0);
                break;
            case 1:
                write0(VARINT_1);
                break;
            case 2:
                write0(VARINT_2);
                break;
            case 3:
                write0(VARINT_3);
                break;
            case 4:
                write0(VARINT_4);
                break;
            case 5:
                write0(VARINT_5);
                break;
            case 6:
                write0(VARINT_6);
                break;
            case 7:
                write0(VARINT_7);
                break;
            case 8:
                write0(VARINT_8);
                break;
            case 9:
                write0(VARINT_9);
                break;
            case 10:
                write0(VARINT_A);
                break;
            case 11:
                write0(VARINT_B);
                break;
            case 12:
                write0(VARINT_C);
                break;
            case 13:
                write0(VARINT_D);
                break;
            case 14:
                write0(VARINT_E);
                break;
            case 15:
                write0(VARINT_F);
                break;
            case 16:
                write0(VARINT_10);
                break;
            case 17:
                write0(VARINT_11);
                break;
            case 18:
                write0(VARINT_12);
                break;
            case 19:
                write0(VARINT_13);
                break;
            case 20:
                write0(VARINT_14);
                break;
            case 21:
                write0(VARINT_15);
                break;
            case 22:
                write0(VARINT_16);
                break;
            case 23:
                write0(VARINT_17);
                break;
            case 24:
                write0(VARINT_18);
                break;
            case 25:
                write0(VARINT_19);
                break;
            case 26:
                write0(VARINT_1A);
                break;
            case 27:
                write0(VARINT_1B);
                break;
            case 28:
                write0(VARINT_1C);
                break;
            case 29:
                write0(VARINT_1D);
                break;
            case 30:
                write0(VARINT_1E);
                break;
            case 31:
                write0(VARINT_1F);
                break;
            default: {
                    int t = v, ix = 0;
                    byte[] b = mTemp;

                    while (true) {
                        b[++ix] = (byte) (v & 0xff);
                        if ((v >>>= 8) == 0)
                            break;
                    }

                    if (t > 0) {
                        // [ 0a e2 => 0a e2 00 ] [ 92 => 92 00 ]
                        if (b[ix] < 0)
                            b[++ix] = 0;
                    } else {
                        // [ 01 ff ff ff => 01 ff ] [ e0 ff ff ff => e0 ]
                        while (b[ix] == (byte) 0xff && b[ix - 1] < 0)
                            ix--;
                    }

                    b[0] = (byte) (VARINT + ix - 1);
                    write0(b, 0, ix + 1);
            }
        }
    }

    private void writeVarint64(long v) throws IOException {
        int i = (int) v;
        if (v == i) {//能用int保存就用int保存，节省空间
            writeVarint32(i);
        } else {
            long t = v;
            int ix = 0;
            byte[] b = mTemp;

            while (true) {
                b[++ix] = (byte) (v & 0xff);
                if ((v >>>= 8) == 0)
                    break;
            }

            if (t > 0) {
                // [ 0a e2 => 0a e2 00 ] [ 92 => 92 00 ]
                if (b[ix] < 0)
                    b[++ix] = 0;
            } else {
                // [ 01 ff ff ff => 01 ff ] [ e0 ff ff ff => e0 ]
                while (b[ix] == (byte) 0xff && b[ix - 1] < 0)
                    ix--;
            }

            b[0] = (byte) (VARINT + ix - 1);
            write0(b, 0, ix + 1);
        }
    }
}