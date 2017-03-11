/**
 * file name : Page.java
 * created at : 3:28:05 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.hx.crawler.crawler.interf.Page;
import com.hx.log.util.BizUtils;
import com.hx.log.util.Tools;

// Page
public class HtmlPage implements Page<HttpResponse> {

    // Response
    private String content;
    private HttpResponse httpResp;
    private List<Header> headers;
    private Map<String, String> cookies;
    private String charset = Tools.DEFAULT_CHARSET;

    // 初始化
    public HtmlPage(Response resp) {
        super();
        headers = new ArrayList<>();
        cookies = new HashMap<>();

        try {
            this.httpResp = resp.returnResponse();
            parseResponse(httpResp);

//			this.content = Tools.getContent(httpResp.getEntity().getContent() );
            this.content = EntityUtils.toString(httpResp.getEntity(), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 响应头中cookie的元素据keys
    static Set<String> nonCookieKeys = new HashSet<>();

    static {
        nonCookieKeys.add(getStdForString("path"));
        nonCookieKeys.add(getStdForString("Max-Age"));
        nonCookieKeys.add(getStdForString("expires"));
        nonCookieKeys.add(getStdForString("domain"));
        // add at 2016.05.02	ref : 深入javaweb p262[Cookie Version1]
        nonCookieKeys.add(getStdForString("secure"));
        nonCookieKeys.add(getStdForString("version"));
        nonCookieKeys.add(getStdForString("comment"));
        nonCookieKeys.add(getStdForString("commentUrl"));
        nonCookieKeys.add(getStdForString("discard"));
        nonCookieKeys.add(getStdForString("port"));
    }

    // setter & getter
    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public HttpResponse getResponse() {
        return httpResp;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public Map<String, String> getCookies() {
        return cookies;
    }

    // got 'response.headers'		add at 2016.05.02
    public List<Header> getHeaders() {
        return headers;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public String getHeader(String key) {
        int idx = HtmlCrawlerConfig.indexOfHeader(headers, key);
        if (idx < 0) {
            return null;
        }
        return headers.get(idx).getValue();
    }

    // 获取Page的内容
    @Override
    public String getContent() {
        return content;
    }

    // 解析响应消息 [cookie, charset等等]
    private void parseResponse(HttpResponse resp) {
        Header[] headers = resp.getAllHeaders();

        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            String headerName = header.getName();
            if (Tools.equalsIgnoreCase(headerName, Tools.RESP_COOKIE_STR)) {
                String headerValue = header.getValue();
                Set<String> nonCookieKeysTmp = nonCookieKeys;
                NameValuePair[] cookie = getCookie(headerValue, nonCookieKeysTmp);
                if (cookie != null) {
                    for (NameValuePair nvp : cookie) {
                        cookies.put(nvp.getName(), nvp.getValue());
                    }
                }
            } else if (Tools.equalsIgnoreCase(headerName, Tools.CONTENT_TYPE)) {
                String charsetTmp = Tools.getStrInRangeWithStart(header.getValue(), "charset=");
                if (!Tools.isEmpty(charsetTmp)) {
                    charset = charsetTmp;
                }
                this.headers.add(header);
            } else {
                this.headers.add(header);
            }
        }
    }

    // 获取响应头中的"Set-Cookie" 中对应的cookie
    // 以; 分割kv对, =分割key 和value
    // 这样解析不太好啊[鲁棒性],, 直接split就行了		add at 2016.05.02
    private NameValuePair[] getCookie(String value, Set<String> nonCookieKeys) {
        NameValuePair[] cookie = new BasicNameValuePair[0];
        int lastIdxSemicolon = 0, idxEqu = value.indexOf(BizUtils.COOKIE_KV_SEP), idxSemicolon = value.indexOf(BizUtils.COOKIE_COOKIE_SEP);
        while ((idxSemicolon > 0) && (idxEqu > 0)) {
            String key = value.substring(lastIdxSemicolon, idxEqu);
            String val = value.substring(idxEqu + 1, idxSemicolon);
            if (!nonCookieKeys.contains(getStdForString(key.trim()))) {
                NameValuePair[] cookieTmp = new BasicNameValuePair[cookie.length + 1];
                System.arraycopy(cookie, 0, cookieTmp, 0, cookie.length);
                cookieTmp[cookieTmp.length - 1] = new BasicNameValuePair(key, val);
                cookie = cookieTmp;
            }

            lastIdxSemicolon = idxSemicolon + 1;
            idxEqu = value.indexOf(BizUtils.COOKIE_KV_SEP, idxSemicolon);
            idxSemicolon = value.indexOf(BizUtils.COOKIE_COOKIE_SEP, idxSemicolon + 1);
        }
        // 处理最后一组kv对
        // 鲁棒性判断 : Set-Cookie: __cfduid=d97fc04a3d79d4567d86b62582fbaa4bc1444897363; expires=Fri, 14-Oct-16 08:22:43 GMT; path=/; domain=.tom365.co; HttpOnly
        if (idxEqu > 0) {
            String key = value.substring(lastIdxSemicolon, idxEqu);
            String val = value.substring(idxEqu + 1);
            if (!nonCookieKeys.contains(getStdForString(key.trim()))) {
                NameValuePair[] cookieTmp = new BasicNameValuePair[cookie.length + 1];
                System.arraycopy(cookie, 0, cookieTmp, 0, cookie.length);
                cookieTmp[cookieTmp.length - 1] = new BasicNameValuePair(key, val);
                cookie = cookieTmp;
            }
        }

        return cookie;
    }

    // 获取统一的大写 或者小写
    private static String getStdForString(String str) {
        return str.toUpperCase();
    }

}
