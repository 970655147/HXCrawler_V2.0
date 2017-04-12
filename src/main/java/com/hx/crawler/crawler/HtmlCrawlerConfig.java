/**
 * file name : CrawlerConfig.java
 * created at : 3:24:46 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.log.util.Tools;

// CrawlerConfig
public class HtmlCrawlerConfig implements CrawlerConfig<Header, String, NameValuePair, String, String> {

    // 获取get, post 的默认的CrawlerConfig方法
    public static HtmlCrawlerConfig get() {
        HtmlCrawlerConfig config = new HtmlCrawlerConfig();
        return config;
    }

    public static HtmlCrawlerConfig post() {
        HtmlCrawlerConfig config = new HtmlCrawlerConfig();
        config.addHeader(Tools.CONTENT_TYPE, Tools.APPLICATION_URL_ENCODED);
        return config;
    }

    // 请求头信息, cookies信息, postData信息
    // 超时配置
    private List<Header> headers;
    private Map<String, String> cookies;
    private List<NameValuePair> data;
    private HttpHost proxy;
    private int timeout;

    // 常量
    private static int DEFAULT_TIMEOUT = 10 * 1000;
    private static Map<String, String> DEFAULT_HEADERS = new HashMap<>();

    static {
        DEFAULT_HEADERS.put(Tools.CONTENT_TYPE, "text/html;charset=utf-8");
        DEFAULT_HEADERS.put(Tools.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//		DEFAULT_HEADERS.put("Accept-Encoding", "gzip, deflate, sdch");
//		DEFAULT_HEADERS.put("Accept-Language", "zh-CN,zh;q=0.8");
//		DEFAULT_HEADERS.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    }

    // 初始化 添加默认的请求头
    public HtmlCrawlerConfig() {
        headers = new ArrayList<>();
        cookies = new HashMap<>();
        data = new ArrayList<>();
        timeout = DEFAULT_TIMEOUT;

        for (Entry<String, String> header : DEFAULT_HEADERS.entrySet()) {
            headers.add(new BasicHeader(header.getKey(), header.getValue()));
        }
    }

    public HtmlCrawlerConfig(HtmlCrawlerConfig config) {
        this();
        addHeaders(config.getHeaders());
        addData(config.getData());
        addCookies(config.getCookies());
        this.timeout = config.getTimeout();
    }

    // getter & setter
    @Override
    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        if (headers == null) {
            this.headers = null;
            return;
        }

        this.headers.clear();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public void setData(List<NameValuePair> data) {
        this.data = data;
    }

    @Override
    public void setData(Map<String, String> data) {
        if (data == null) {
            this.data = null;
            return;
        }

        this.data.clear();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            addData(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getCookies() {
        return cookies;
    }

    @Override
    public List<NameValuePair> getData() {
        return data;
    }

    @Override
    public HttpHost getProxy() {
        return proxy;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public HtmlCrawlerConfig addHeader(String key, String value) {
        int idx = indexOfHeader(headers, key);
        if (idx >= 0) {
            headers.remove(idx);
        }
        headers.add(new BasicHeader(key, value));

        return this;
    }

    @Override
    public HtmlCrawlerConfig addHeaders(List<Header> headers) {
        if (headers == null) {
            return this;
        }
        for (Header header : headers) {
            addHeader(header.getName(), header.getValue());
        }
        return this;
    }

    @Override
    public HtmlCrawlerConfig addHeaders(Map<String, String> headers) {
        if (headers == null) {
            return this;
        }
        for (Entry<String, String> entry : headers.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public HtmlCrawlerConfig addCookie(String key, String value) {
        cookies.put(key, value);
        return this;
    }

    @Override
    public HtmlCrawlerConfig addCookies(Map<String, String> cookies) {
        if (cookies == null) {
            return this;
        }
        for (Entry<String, String> header : cookies.entrySet()) {
            addCookie(header.getKey(), header.getValue());
        }
        return this;
    }

    @Override
    public HtmlCrawlerConfig addData(String key, String value) {
        int idx = indexOfData(data, key);
        if (idx >= 0) {
            data.remove(idx);
        }
        data.add(new BasicNameValuePair(key, value));

        return this;
    }

    @Override
    public HtmlCrawlerConfig addData(List<NameValuePair> datas) {
        if (datas == null) {
            return this;
        }
        for (NameValuePair data : datas) {
            addData(data.getName(), data.getValue());
        }
        return this;
    }

    @Override
    public HtmlCrawlerConfig addData(Map<String, String> datas) {
        if (datas == null) {
            return this;
        }
        for (Entry<String, String> data : datas.entrySet()) {
            addData(data.getKey(), data.getValue());
        }
        return this;
    }

    // key 在headers, data中的索引
    static int indexOfHeader(List<Header> headers, String key) {
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getName().equals(key)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 查找data中key对应的条目的索引
     *
     * @param data 给定的集合
     * @param key  查找的key
     * @return int
     * @throws
     * @author 970655147 created at 2017-03-11 15:48
     */
    private static int indexOfData(List<NameValuePair> data, String key) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equals(key)) {
                return i;
            }
        }

        return -1;
    }

}
