/**
 * file name : Crawler.java
 * created at : 8:23:27 PM Jul 31, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.InputStream;

/**
 * Cralwer �淶
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 7:41 PM
 */
public abstract class Crawler<ResponseType, HeaderValType, DataValType, CookieValType> {

    /**
     * ��ȡ��ǰCrawler��ϵĽű�����[Context]����
     */
    protected ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> scriptParameter;

    /**
     * ��Ŀ��url����get����
     *
     * @param url    Ŀ��url
     * @param config ����
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> getPage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config) throws IOException;

    public abstract Page<ResponseType> getPage(String url) throws IOException;

    /**
     * ��Ŀ��url����post����
     *
     * @param url         Ŀ��url
     * @param config      ����
     * @param bodyData    �������е�����
     * @param contentType contentType
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config, String bodyData, ContentType contentType) throws IOException;

    /**
     * ��Ŀ��url����post����           -- add at 2016.06.02
     *
     * @param url         Ŀ��url
     * @param config      ����
     * @param inputStream �������е���
     * @param contentType contentType
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<HttpResponse> postPage(String url, CrawlerConfig<String, String, String> config, InputStream inputStream, ContentType contentType) throws IOException;

    public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config) throws IOException;

    public abstract Page<ResponseType> postPage(String url) throws IOException;

    /**
     * ��Ŀ��url����put����            -- add at 2017.03.04
     *
     * @param url    Ŀ��url
     * @param config ����
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> putPage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config) throws IOException;

    public abstract Page<ResponseType> putPage(String url) throws IOException;

    /**
     * ��Ŀ��url����delete����            -- add at 2017.03.04
     *
     * @param url    Ŀ��url
     * @param config ����
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> deletePage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config) throws IOException;

    public abstract Page<ResponseType> deletePage(String url) throws IOException;

    /**
     * ��ȡscriptParameter
     *
     * @return com.hx.crawler.crawler.interf.ScriptParameter<ResponseType,HeaderValType,DataValType,CookieValType>
     * @author Jerry.X.He
     * @date 5/10/2017 7:45 PM
     * @since 1.0
     */
    public ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> getScriptParameter() {
        return scriptParameter;
    }

    /**
     * ����scriptParameter
     *
     * @param scriptParameter ������scriptParameter
     * @return void
     * @author Jerry.X.He
     * @date 5/10/2017 7:45 PM
     * @since 1.0
     */
    public void setScriptParameter(ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> scriptParameter) {
        this.scriptParameter = scriptParameter;
    }


    // ----------------- ���� -----------------------


}
