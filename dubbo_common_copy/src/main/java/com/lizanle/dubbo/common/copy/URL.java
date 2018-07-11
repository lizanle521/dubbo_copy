package com.lizanle.dubbo.common.copy;

import com.lizanle.dubbo.common.copy.utils.CollectionUtils;
import com.lizanle.dubbo.common.copy.utils.NetUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * URL - Uniform Resource Locator (Immutable,ThreadSafe)
 * <p>
 *     url Example:
 *     <ul>
 *          <li>
 *              http://www.facebook.com/friends?param1=value1&param2=value2
 *          </li>
 *          <li>
 *              http://username:password@10.20.10.145:8080/list?version=1.0
 *          </li>
 *          <li>
 *              registry://192.168.10.162:8080/com.lizanle.service.DemoService?param1=value1&param2=value2
 *          </li>
 *     </ul>
 * </p>
 */
public final class URL  implements Serializable{
    private static final long serialVersionUID = -9065582620789545190L;

    private final String protocol;

    private final String username;

    private final String password;

    private final String host;

    private final int port;

    private final String path;

    private final Map<String,String> parameters;

    // cache //

    private volatile transient Map<String,Number> numbers;

    private volatile transient Map<String,URL> urls;

    private volatile transient String ip;

    private volatile transient String full;

    private volatile transient String identity;

    private volatile transient String parameter;

    private volatile transient String string;

    protected URL(){
        this.protocol = null;
        this.host = null;
        this.username = null;
        this.password = null;
        this.port = 0;
        this.path = null;
        this.parameters = null;
    }

    public URL(String protocol, String host, int port) {
      this(protocol,null,null,host,port,null,(Map<String, String>) null);
    }

    public URL(String protocol, String host, int port, String[] pairs) {// 变长参数...与下面的path参数冲突，改为数组
        this(protocol,null,null,host,port,null, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, Map<String, String> parameters) {
       this(protocol,null,null,host,port,null,parameters);
    }

    public URL(String protocol, String host, int port, String path) {
        this(protocol,null,null,host,port,path,(Map<String,String>)null);
    }

    public URL(String protocol, String host, int port, String path, String... pairs) {
        this(protocol,null,null,host,port,path,CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this(protocol,null,null,host,port,path,parameters);
    }

    public URL(String protocol, String username, String password, String host, int port, String path) {
        this(protocol,username,password,host,port,path,(Map<String,String>)null);
    }

    public URL(String protocol, String username, String password, String host, int port, String path,String... pairs) {
        this(protocol,username,password,host,port,path,CollectionUtils.toStringMap(pairs));
    }


    public URL(String protocol, String username, String password, String host, int port, String path, Map<String, String> parameters) {
        if((username == null || username.length() == 0)
                &&(password != null && password.length() > 0)){
            throw new IllegalArgumentException("invalid url,username without password!");
        }
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port < 0 ? 0:port;
        // 去掉path的/前缀
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        if(parameters == null){
            parameters = new HashMap<>();
        }else{
            parameters = new HashMap<>(parameters);
        }
        // 参数不能再改变
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    /**
     * parse String url
     * @param url url String
     * @return URL instance
     */
    public static URL valueOf(String url){
        if(url == null || url.trim().length() == 0){
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        String path = null;
        int port = 0;
        Map<String,String> parameters = null;

        int i = url.indexOf("?");// 参数和内容的分隔符 ？
        if(i > 0){
            String[] paramsParts = url.substring(i + 1).split("\\&");
            parameters = new HashMap<>();
            for (String part : paramsParts) {
                int j = part.indexOf("=");
                if(j > 0){
                    parameters.put(part.substring(0,j),part.substring(j+1));
                }else{
                    parameters.put(part,part);
                }
            }
            url = url.substring(0,i);//取头部
        }
        i = url.indexOf("://");
        if(i >= 0){
            if(i == 0) throw new IllegalStateException("url missing protocol:\""+url+"\"");
            protocol = url.substring(0,i); // 取协议
            url = url.substring(i+3);//取username,password,host,port,path部分
        }else{
            // case file:/path/to/file.txt
            i = url.indexOf(":/");
            if(i >= 0){
                if(i == 0)  throw new IllegalStateException("url missing protocol:\""+url+"\"");
                protocol = url.substring(0,i); //取协议
                url = url.substring(i + 1);//取username,password,host,port,path部分
            }
        }
        i = url.indexOf("/");
        if(i >= 0){
            path = url.substring(i + 1);//取path
            url = url.substring(0,i);
        }
        i = url.indexOf("@");
        if(i >= 0){
            username = url.substring(0,i);
            int j = username.indexOf(":");
            if(j >= 0){
                username = username.substring(0,j);
                password = username.substring(j + 1);
            }
            url = url.substring(i+1);
        }
        i = url.indexOf(":");
        if(i >= 0 && i < url.length() - 1){
            port = Integer.parseInt(url.substring(i+1));
            url = url.substring(0,i);
        }

        if(url.length() > 0 ) {
            host = url;
        }
        return new URL(protocol,username,password,host,port,path,parameters);
    }

    public static String encode(String value){
        if(value == null || value.length() == 0){
            return "";
        }
        try {
            return URLEncoder.encode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    public static String decode(String value){
        if(value == null || value.length() == 0){
            return "";
        }
        try {
            return URLDecoder.decode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    public String getProtocol() {
        return protocol;
    }
    public URL setProtocol(String protocol) {
        return new URL(protocol,username,password,host,port,path,getParameters());
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public URL setUsername(String username) {
        return new URL(protocol,username,password,host,port,path,getParameters());
    }



    public String getPassword() {
        return password;
    }

    public URL setPassword(String password){
        return new URL(protocol,username,password,host,port,path,getParameters());
    }

    public URL setHost(String host){
        return new URL(protocol,username,password,host,port,path,getParameters());
    }

    public int getPort() {
        return port;
    }

    public int getPort(int defaultPort){
        return port < 0 ? defaultPort:port;
    }

    public URL setPort(int port){
        return new URL(protocol,username,password,host,port,path,getParameters());
    }

    public String getPath() {
        return path;
    }

    public URL setPath(String path){
        return new URL(protocol,username,password,host,port,path,getParameters());
    }

    public String getAbsolutePath(){
        if(path != null && !path.startsWith("/")){
            return "/" + path;
        }
        return path;
    }

    public String getAddress(){
        return port <= 0 ? host : host + ":" + port;
    }

    public URL setAddress(String address){
        int i = address.lastIndexOf(":");
        String host;
        int port = this.port;
        if(i >= 0 && i < address.length() - 1){
            port = Integer.parseInt(address.substring(i + 1));
            host = address.substring(0,i);
        }else if(i < 0){
            host = address;
        }else{
            host = address.substring(0,i);
        }
        return new URL(protocol,username,password,host,port,path,getParameters());
    }

    public String getAuthority() {
        if ((username == null || username.length() == 0)
                && (password == null || password.length() == 0)) {
            return null;
        }
        return (username == null ? "" : username)
                + ":" + (password == null ? "" : password);
    }


    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * 获取IP地址
     * <p>
     *     请注意，如果和socket的地址对比
     *     或用地址作为map的key查找
     *     请使用Ip而不是host
     *     否则配置域名会有问题 ？？
     * </p>
     * @return
     */
    public String getIp(){
        if(ip == null){
            ip = NetUtils.getIpByHost(host);
        }
        return ip;
    }

    /**
     * 获取某个对应的参数
     * @param key
     * @return
     */
    public String getParameter(String key){
        String s = parameters.get(key);
        if(s == null || s.length() == 0){
            s = parameters.get(Constants.DEFAULT_KEY_PREFIX + key);
        }
        return s;
    }

    public String getParameter(String key,String defaultValue){
        String value = getParameter(key);
        if(value == null || value.length() == 0){
            return defaultValue;
        }
        return value;
    }

    public String[] getParameter(String key,String[] parameter){
        String value = getParameter(key);
        if(value == null || value.length() == 0){
            return parameter;
        }
        return Constants.COMMA_SPLIT_PATTERN.split(value);
    }

    public String getParameterAndDecode(String key){
        return getParameterAndDecode(key,null);
    }

    public String getParameterAndDecode(String key,String defaultValue){
        return decode(getParameter(key,defaultValue));
    }

    public Map<String, Number> getNumbers() {
        if(numbers == null){// 允许并发重建
            numbers = new ConcurrentHashMap<>();
        }
        return numbers;
    }

    public Map<String, URL> getUrls() {
        if(urls == null){// 允许并发重建
            urls = new ConcurrentHashMap<>();
        }
        return urls;
    }

    public URL addParameter(String key, String value) {
        if (key == null || key.length() == 0
                || value == null || value.length() == 0) {
            return this;
        }
        // 如果没有修改，直接返回。
        if (value.equals(getParameters().get(key))) { // value != null
            return this;
        }

        Map<String, String> map = new HashMap<String, String>(getParameters());
        map.put(key, value);
        return new URL(protocol, username, password, host, port, path, map);
    }

    public URL removeParameter(String key) {
        if (key == null || key.length() == 0) {
            return this;
        }
        return removeParameters(key);
    }

    public URL removeParameters(Collection<String> keys) {
        if (keys == null || keys.size() == 0) {
            return this;
        }
        return removeParameters(keys.toArray(new String[0]));
    }

    public URL removeParameters(String... keys) {
        if (keys == null || keys.length == 0) {
            return this;
        }
        Map<String, String> map = new HashMap<String, String>(getParameters());
        for (String key : keys) {
            map.remove(key);
        }
        if (map.size() == getParameters().size()) {
            return this;
        }
        return new URL(protocol, username, password, host, port, path, map);
    }


}
