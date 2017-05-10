/**
 * file name : CrawlerConfig.java
 * created at : 11:24:46 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import org.apache.http.HttpHost;

import java.util.Map;

/**
 * 爬虫的相关配置[Context]
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 8:14 PM
 */
public interface CrawlerConfig<HeaderValType, DataValType, CookieValType> {

    /**
     * getter & setter
     */
    void setHeaders(Map<String, HeaderValType> headers);

    void setData(Map<String, DataValType> data);

    void setCookies(Map<String, CookieValType> cookies);

    void setTimeout(int timeout);

    void setProxy(HttpHost proxy);

    Map<String, HeaderValType> getHeaders();

    Map<String, CookieValType> getCookies();

    Map<String, DataValType> getData();

    int getTimeout();

    HttpHost getProxy();

    /**
     * 添加给定的kv到请求头中
     *
     * @param key   给定的请求头的key
     * @param value 请求头的value
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addHeader(String key, HeaderValType value);

    /**
     * 添加给定的多个kv到请求头中
     *
     * @param headers 给定的需要添加的多个请求头kv对
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addHeaders(Map<String, HeaderValType> headers);

    /**
     * 添加给定的kv到cookie中
     *
     * @param key   给定的cookie的key
     * @param value cookie的value
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addCookie(String key, CookieValType value);

    /**
     * 添加给定的多个kv到cookies中
     *
     * @param cookies 给定的需要添加的多个cookies的kv对
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addCookies(Map<String, CookieValType> cookies);

    /**
     * 添加给定的kv到额外数据块中
     *
     * @param key   给定的额外数据块的key
     * @param value 额外数据块的value
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addData(String key, DataValType value);

    /**
     * 添加给定的多个kv到额外数据块中
     *
     * @param data 给定的需要添加的多个额外数据块kv对
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addData(Map<String, DataValType> data);

}
