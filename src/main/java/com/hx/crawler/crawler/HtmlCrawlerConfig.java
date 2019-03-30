/**
 * file name : CrawlerConfig.java
 * created at : 3:24:46 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.log.util.Tools;
import org.apache.http.HttpHost;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HtmlCrawler 关联的一个 CrawlerConfig 的简单实现
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 7:53 PM
 */
public class HtmlCrawlerConfig implements CrawlerConfig<String, String, String> {

    /**
     * 默认的超时时间
     */
    private static int DEFAULT_TIMEOUT = 10 * 1000;
    /**
     * 默认的请求头
     */
    private static Map<String, String> DEFAULT_HEADERS = new HashMap<>();

    static {
        DEFAULT_HEADERS.put(Tools.CONTENT_TYPE, "text/html;charset=utf-8");
        DEFAULT_HEADERS.put(Tools.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//		DEFAULT_HEADERS.put("Accept-Encoding", "gzip, deflate, sdch");
//		DEFAULT_HEADERS.put("Accept-Language", "zh-CN,zh;q=0.8");
//		DEFAULT_HEADERS.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    }

    /**
     * 获取get请求的默认的CrawlerConfig方法
     *
     * @return com.hx.crawler.crawler.HtmlCrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:54 PM
     * @since 1.0
     */
    public static HtmlCrawlerConfig get() {
        HtmlCrawlerConfig config = new HtmlCrawlerConfig();
        return config;
    }

    /**
     * 获取post请求的默认的CrawlerConfig方法
     *
     * @return com.hx.crawler.crawler.HtmlCrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:54 PM
     * @since 1.0
     */
    public static HtmlCrawlerConfig post() {
        HtmlCrawlerConfig config = new HtmlCrawlerConfig();
        config.addHeader(Tools.CONTENT_TYPE, Tools.APPLICATION_URL_ENCODED);
        return config;
    }

    /**
     * 请求头信息
     */
    private Map<String, String> headers;
    /**
     * cookies 信息
     */
    private Map<String, String> cookies;
    /**
     * 额外的数据信息
     */
    private Map<String, String> data;
    /**
     * 代理信息
     */
    private HttpHost proxy;
    /**
     * 超时配置
     */
    private int timeout;
    /**
     * 连接超时配置
     */
    private int connectionTimeout;
    /**
     * socket 超时配置
     */
    private int socketTimeout;

    /**
     * 初始化 添加默认的请求头
     *
     * @since 1.0
     */
    public HtmlCrawlerConfig() {
        headers = new HashMap<>();
        cookies = new HashMap<>();
        data = new HashMap<>();
        timeout = DEFAULT_TIMEOUT;

        for (Entry<String, String> header : DEFAULT_HEADERS.entrySet()) {
            headers.put(header.getKey(), header.getValue());
        }
    }

    public HtmlCrawlerConfig(HtmlCrawlerConfig config) {
        this();
        addHeaders(config.getHeaders());
        addData(config.getData());
        addCookies(config.getCookies());
        this.timeout = config.getTimeout();
        this.connectionTimeout = config.getConnectionTimeout();
        this.socketTimeout = config.getSocketTimeout();
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        if (headers == null) {
            return;
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public void setData(Map<String, String> data) {
        this.data.clear();
        if (data == null) {
            return;
        }

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
    public void setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
    }

    @Override
    public void setSocketTimeout(int timeout) {
        this.socketTimeout = timeout;
    }

    @Override
    public Map<String, String> getCookies() {
        return cookies;
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
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public int getSocketTimeout() {
        return socketTimeout;
    }

    @Override
    public HtmlCrawlerConfig addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    @Override
    public HtmlCrawlerConfig addHeaders(Map<String, String> headers) {
        if (Tools.isEmpty(headers)) {
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
        if (Tools.isEmpty(cookies)) {
            return this;
        }
        for (Entry<String, String> header : cookies.entrySet()) {
            addCookie(header.getKey(), header.getValue());
        }
        return this;
    }

    @Override
    public HtmlCrawlerConfig addData(String key, String value) {
        data.put(key, value);
        return this;
    }

    @Override
    public HtmlCrawlerConfig addData(Map<String, String> data) {
        if (Tools.isEmpty(data)) {
            return this;
        }
        for (Entry<String, String> entry : data.entrySet()) {
            addData(entry.getKey(), entry.getValue());
        }
        return this;
    }

}
