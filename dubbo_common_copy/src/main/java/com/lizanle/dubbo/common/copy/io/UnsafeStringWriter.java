package com.lizanle.dubbo.common.copy.io;

import java.io.IOException;
import java.io.Writer;

/**
 * Thread unsafe String writer
 */
public class UnsafeStringWriter extends Writer {

    private StringBuilder mBuffer;

    public UnsafeStringWriter() {
        lock = mBuffer = new StringBuilder();
    }

    public UnsafeStringWriter(int size) {
        if(size < 0){
            throw new IllegalStateException("negative buff size");
        }
        lock = this.mBuffer = new StringBuilder(size);
    }

    @Override
    public void write(int c) throws IOException {
        mBuffer.append((char) c);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        mBuffer.append(cbuf,0,cbuf.length);
    }


    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if((off < 0) || (off > cbuf.length) || (len < 0) || (off+len>cbuf.length) || (off+len) < 0){
            throw new IndexOutOfBoundsException();
        }
        if(len > 0){
            mBuffer.append(cbuf,off,len);
        }
    }

    @Override
    public void write(String str)  {
        mBuffer.append(str);
    }

    @Override
    public void write(String str, int off, int len)   {
        mBuffer.append(str.substring(off,off+len));
    }

    public Writer append(CharSequence csq){
        if(csq == null){
            this.write("null");
        }else{
            write(csq.toString());
        }
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        CharSequence cs = (csq == null ? "null" : csq);
        write(cs.subSequence(start,end).toString());
        return this;
    }

    @Override
    public Writer append(char c) throws IOException {
        mBuffer.append(c);
        return this;
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String toString() {
        return mBuffer.toString();
    }
}
