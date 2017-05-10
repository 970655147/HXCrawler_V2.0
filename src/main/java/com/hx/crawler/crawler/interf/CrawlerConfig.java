/**
 * file name : CrawlerConfig.java
 * created at : 11:24:46 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import org.apache.http.HttpHost;

import java.util.Map;

/**
 * ������������[Context]
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
     * ��Ӹ�����kv������ͷ��
     *
     * @param key   ����������ͷ��key
     * @param value ����ͷ��value
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addHeader(String key, HeaderValType value);

    /**
     * ��Ӹ����Ķ��kv������ͷ��
     *
     * @param headers ��������Ҫ��ӵĶ������ͷkv��
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addHeaders(Map<String, HeaderValType> headers);

    /**
     * ��Ӹ�����kv��cookie��
     *
     * @param key   ������cookie��key
     * @param value cookie��value
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addCookie(String key, CookieValType value);

    /**
     * ��Ӹ����Ķ��kv��cookies��
     *
     * @param cookies ��������Ҫ��ӵĶ��cookies��kv��
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addCookies(Map<String, CookieValType> cookies);

    /**
     * ��Ӹ�����kv���������ݿ���
     *
     * @param key   �����Ķ������ݿ��key
     * @param value �������ݿ��value
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addData(String key, DataValType value);

    /**
     * ��Ӹ����Ķ��kv���������ݿ���
     *
     * @param data ��������Ҫ��ӵĶ���������ݿ�kv��
     * @return com.hx.crawler.crawler.interf.CrawlerConfig
     * @author Jerry.X.He
     * @date 5/10/2017 7:38 PM
     * @since 1.0
     */
    CrawlerConfig<HeaderValType, DataValType, CookieValType> addData(Map<String, DataValType> data);

}
