package com.lizanle.dubbo.common.copy.extension.ext2;

import com.lizanle.dubbo.common.copy.URL;

public class URLHolder {
    private Double Num;

    private URL url;

    private String name;

    private int age;

    public Double getNum() {
        return Num;
    }

    public void setNum(Double num) {
        Num = num;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
