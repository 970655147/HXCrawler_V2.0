/**
 * file name : Crawler.java
 * created at : 3:24:26 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.Page;
import com.hx.log.util.Tools;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ����httpЭ��, html��Ӧ�ĵ�������ʵ��
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 7:46 PM
 */
public class HtmlCrawler extends Crawler<HttpResponse, String, String, String> {

    /**
     * url �Ͳ�ѯ�ַ����ķָ���
     */
    public static final String URL_QUERYSTR_SEP = "?";
    /**
     * getInstance ��ȡ����Ψһʵ��
     */
    private static HtmlCrawler instance;

    /**
     * ��ȡȫ�ֵ�һ��������HtmlCrawler
     *
     * @return com.hx.crawler.crawler.HtmlCrawler
     * @author Jerry.X.He
     * @date 5/10/2017 7:47 PM
     * @since 1.0
     */
    public static HtmlCrawler getInstance() {
        if (instance == null) {
            synchronized (HtmlCrawler.class) {
                if (instance == null) {
                    instance = newInstance();
                }
            }
        }

        return instance;
    }

    /**
     * ����һ���µ�HtmlCrawler
     *
     * @return com.hx.crawler.crawler.HtmlCrawler
     * @author Jerry.X.He
     * @date 5/10/2017 7:47 PM
     * @since 1.0
     */
    public static HtmlCrawler newInstance() {
        return new HtmlCrawler();
    }

    @Override
    public Page<HttpResponse> getPage(String url) throws IOException {
        return getPage(url, HtmlCrawlerConfig.get());
    }

    @Override
    public Page<HttpResponse> getPage(String url, CrawlerConfig<String, String, String> config) throws IOException {
        Tools.assert0(url != null, "url can't be null ");
        Tools.assert0(config != null, "CrawlerConfig can't be null ");

        url = encapQueryStrIfNeeded(url, config);
        Request req = Request.Get(url);
        return doExecute(req, config, config.getProxy());
    }

    @Override
    public Page<HttpResponse> postPage(String url) throws IOException {
        return postPage(url, new HtmlCrawlerConfig());
    }

    @Override
    public Page<HttpResponse> postPage(String url, CrawlerConfig<String, String, String> config) throws IOException {
        Tools.assert0(url != null, "url can't be null ");
        Tools.assert0(config != null, "CrawlerConfig can't be null ");

        Request req = encapPostReq(url, config);
        Response resp = req.execute();
        return new HtmlPage(resp);
    }

    @Override
    public Page<HttpResponse> postPage(String url, CrawlerConfig<String, String, String> config, String bodyData, ContentType contentType) throws IOException {
        Tools.assert0(url != null, "url can't be null ");
        Tools.assert0(config != null, "CrawlerConfig can't be null ");
        Tools.assert0(bodyData != null, "bodyData can't be null ");
        Tools.assert0(contentType != null, "contentType can't be null ");

        Request req = encapPostReq(url, config);
        req.bodyString(bodyData, contentType);
        Response resp = req.execute();
        return new HtmlPage(resp);
    }

    @Override
    public Page<HttpResponse> postPage(String url, CrawlerConfig<String, String, String> config, InputStream inputStream, ContentType contentType) throws IOException {
        Tools.assert0(url != null, "url can't be null ");
        Tools.assert0(config != null, "CrawlerConfig can't be null ");
        Tools.assert0(inputStream != null, "inputStream can't be null ");
        Tools.assert0(contentType != null, "contentType can't be null ");

        Request req = encapPostReq(url, config);
        req.bodyStream(inputStream, contentType);
        Response resp = req.execute();
        return new HtmlPage(resp);
    }

    @Override
    public Page<HttpResponse> putPage(String url) throws IOException {
        return putPage(url, new HtmlCrawlerConfig());
    }

    @Override
    public Page<HttpResponse> putPage(String url, CrawlerConfig<String, String, String> config) throws IOException {
        Tools.assert0(url != null, "url can't be null ");
        Tools.assert0(config != null, "CrawlerConfig can't be null ");

        url = encapQueryStrIfNeeded(url, config);
        Request req = Request.Put(url);
        return doExecute(req, config, config.getProxy());
    }

    @Override
    public Page<HttpResponse> deletePage(String url) throws IOException {
        return deletePage(url, new HtmlCrawlerConfig());
    }

    @Override
    public Page<HttpResponse> deletePage(String url, CrawlerConfig<String, String, String> config) throws IOException {
        Tools.assert0(url != null, "url can't be null ");
        Tools.assert0(config != null, "CrawlerConfig can't be null ");

        url = encapQueryStrIfNeeded(url, config);
        Request req = Request.Delete(url);
        return doExecute(req, config, config.getProxy());
    }

    // ----------------- �������� -----------------------

    /**
     * ��װ����������, ����������, ��������ػ��� [����get, put, delete����]
     *
     * @param req    ����������
     * @param config ����������Ϣ
     * @param proxy  ������Ϣ
     * @return com.hx.crawler.crawler.HtmlPage
     * @author Jerry.X.He
     * @date 5/10/2017 7:49 PM
     * @since 1.0
     */
    private HtmlPage doExecute(Request req, CrawlerConfig<String, String, String> config,
                               HttpHost proxy) throws IOException {
        req.connectTimeout(config.getTimeout());
        setHeadersAndCookies(req, config);
        if (proxy != null) {
            req.viaProxy(proxy);
        }

        Response resp = req.execute();
        return new HtmlPage(resp);
    }

    /**
     * ��config�е����� ���õ�request��
     *
     * @param req    ������request
     * @param config ������config
     * @return void
     * @author Jerry.X.He
     * @date 5/10/2017 7:49 PM
     * @since 1.0
     */
    private static void config(Request req, CrawlerConfig<String, String, String> config) {
        setHeadersAndCookies(req, config);
        req.bodyForm(map2NameValuePair(config.getData()));
    }

    /**
     * Ϊrequest��������ͷ & cookies
     *
     * @param req    ������request
     * @param config ������config
     * @return void
     * @author Jerry.X.He
     * @date 5/10/2017 7:50 PM
     * @since 1.0
     */
    private static void setHeadersAndCookies(Request req, CrawlerConfig<String, String, String> config) {
        for (Map.Entry<String, String> entry : config.getHeaders().entrySet()) {
            if (Tools.equalsIgnoreCase(entry.getKey(), Tools.COOKIE_STR)) {
                config.addCookies(Tools.getCookiesByCookieStr(entry.getValue()));
                continue;
            }

            req.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
        }

        // add "&& ((config.getCookies().size() > 0))" incase of have no cookie		add at 2016.04.07
        // update incase of exists 'COOKIE' in header [can't add 'config.getCookies''s 'Cookie' ]		add at 2016.05.02
        if ((config.getCookies().size() > 0)) {
            req.addHeader(Tools.COOKIE_STR, Tools.getCookieStr(config.getCookies()));
        }
    }

    /**
     * ��������Map<String, String> ת��Ϊ List<NameValuePair>
     *
     * @param data ���������
     * @return java.util.List<org.apache.http.NameValuePair>
     * @author Jerry.X.He
     * @date 5/10/2017 7:50 PM
     * @since 1.0
     */
    private static List<NameValuePair> map2NameValuePair(Map<String, String> data) {
        if (Tools.isEmpty(data)) {
            return Collections.emptyList();
        }

        List<NameValuePair> result = new ArrayList<>(data.size());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return result;
    }


    /**
     * �����Ҫ[get, put, delete����]��װ���ݵ���ѯ�ַ���, ������ƴ�ӵ�url����
     *
     * @param url    ������url
     * @param config ����
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 7:51 PM
     * @since 1.0
     */
    private String encapQueryStrIfNeeded(String url, CrawlerConfig<String, String, String> config) {
        String queryStr = Tools.encapQueryString(config.getData());
        StringBuilder sb = new StringBuilder(url.length() + queryStr.length() + URL_QUERYSTR_SEP.length());

        sb.append(url);
        if (!Tools.isEmpty(queryStr) && (!url.contains(URL_QUERYSTR_SEP))) {
            sb.append(URL_QUERYSTR_SEP);
        }
        sb.append(queryStr);
        return sb.toString();
    }

    /**
     * ��װpost��request����, ��������ͷ, ����, ����ȵ�
     *
     * @param url    ������url
     * @param config ��������������
     * @return org.apache.http.client.fluent.Request
     * @author 970655147 created at 2017-03-11 15:38
     */
    private Request encapPostReq(String url, CrawlerConfig<String, String, String> config) {
        Request req = Request.Post(url);
        req.connectTimeout(config.getTimeout());
        config(req, config);
        HttpHost proxy = config.getProxy();
        if (proxy != null) {
            req.viaProxy(proxy);
        }

        return req;
    }

}
