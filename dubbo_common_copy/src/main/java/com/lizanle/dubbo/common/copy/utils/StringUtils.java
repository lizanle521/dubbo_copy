package com.lizanle.dubbo.common.copy.utils;

import com.lizanle.dubbo.common.copy.io.UnsafeStringWriter;

import java.io.PrintWriter;

public class StringUtils {

    public static String toString(Throwable e){
        UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter printWriter = new PrintWriter(w);
        printWriter.print(e.getClass().getName());
        if(e.getMessage() != null){
            printWriter.print(": "+e.getMessage());
        }
        printWriter.println();
        try {
            e.printStackTrace(printWriter);
            return w.toString();
        }finally {
            printWriter.close();
        }
    }

    public static boolean isBlank(String name) {
        return name == null || name.length() == 0;
    }
}
