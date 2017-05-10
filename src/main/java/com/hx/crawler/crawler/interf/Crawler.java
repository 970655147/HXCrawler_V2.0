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
 * Cralwer 规范
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 7:41 PM
 */
public abstract class Crawler<ResponseType, HeaderValType, DataValType, CookieValType> {

    /**
     * 获取当前Crawler组合的脚本参数[Context]对象
     */
    protected ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> scriptParameter;

    /**
     * 向目标url发送get请求
     *
     * @param url    目标url
     * @param config 配置
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> getPage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config) throws IOException;

    public abstract Page<ResponseType> getPage(String url) throws IOException;

    /**
     * 向目标url发送post请求
     *
     * @param url         目标url
     * @param config      配置
     * @param bodyData    请求体中的数据
     * @param contentType contentType
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config, String bodyData, ContentType contentType) throws IOException;

    /**
     * 向目标url发送post请求           -- add at 2016.06.02
     *
     * @param url         目标url
     * @param config      配置
     * @param inputStream 请求体中的流
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
     * 向目标url发送put请求            -- add at 2017.03.04
     *
     * @param url    目标url
     * @param config 配置
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> putPage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config) throws IOException;

    public abstract Page<ResponseType> putPage(String url) throws IOException;

    /**
     * 向目标url发送delete请求            -- add at 2017.03.04
     *
     * @param url    目标url
     * @param config 配置
     * @return com.hx.crawler.crawler.interf.Page
     * @author Jerry.X.He
     * @date 5/10/2017 7:41 PM
     * @since 1.0
     */
    public abstract Page<ResponseType> deletePage(String url, CrawlerConfig<HeaderValType, DataValType, CookieValType> config) throws IOException;

    public abstract Page<ResponseType> deletePage(String url) throws IOException;

    /**
     * 获取scriptParameter
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
     * 配置scriptParameter
     *
     * @param scriptParameter 给定的scriptParameter
     * @return void
     * @author Jerry.X.He
     * @date 5/10/2017 7:45 PM
     * @since 1.0
     */
    public void setScriptParameter(ScriptParameter<ResponseType, HeaderValType, DataValType, CookieValType> scriptParameter) {
        this.scriptParameter = scriptParameter;
    }


    // ----------------- 待续 -----------------------


}
