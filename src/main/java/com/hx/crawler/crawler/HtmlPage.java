/**
 * file name : Page.java
 * created at : 3:28:05 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.util.StringCachedInputStream;
import com.hx.log.biz.BizUtils;
import com.hx.log.util.Log;
import com.hx.log.util.Tools;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Page

/**
 * 对应于HtmlCrawler 的接收响应结果的实体
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/10/2017 8:13 PM
 */
public class HtmlPage implements Page<HttpResponse> {

    /**
     * 响应头中cookie的元素据keys
     */
    private static Set<String> NON_COOKIE_KESY = new HashSet<>();
    /**
     * 上一次响应得到的字节长度
     */
    private static int PREV_BYTES_LENGTH = 100;

    static {
        NON_COOKIE_KESY.add(getStdForString("path"));
        NON_COOKIE_KESY.add(getStdForString("Max-Age"));
        NON_COOKIE_KESY.add(getStdForString("expires"));
        NON_COOKIE_KESY.add(getStdForString("domain"));
        // add at 2016.05.02	ref : 深入javaweb p262[Cookie Version1]
        NON_COOKIE_KESY.add(getStdForString("secure"));
        NON_COOKIE_KESY.add(getStdForString("version"));
        NON_COOKIE_KESY.add(getStdForString("comment"));
        NON_COOKIE_KESY.add(getStdForString("commentUrl"));
        NON_COOKIE_KESY.add(getStdForString("discard"));
        NON_COOKIE_KESY.add(getStdForString("port"));
    }

    /**
     * 缓存响应的字节, 是 getInputStream 之后, 现在已经读取了的字节数据
     */
    private ByteArrayBuffer bytes;
    /**
     * 缓存响应的内容
     */
    private String cachedContent;
    /**
     * 服务端的响应对象
     */
    private HttpResponse httpResp;
    /**
     * 解析的响应头
     */
    private Map<String, String> headers;
    /**
     * 解析的相应的cookie列表
     */
    private Map<String, String> cookies;
    /**
     * 服务端响应的字符集
     */
    private String charset = Tools.DEFAULT_CHARSET;
    /**
     * 响应是否被消费了
     */
    private boolean respConsumed;

    /**
     * 初始化
     *
     * @param resp 服务端的额响应
     * @since 1.0
     */
    public HtmlPage(Response resp) {
        headers = new HashMap<>();
        cookies = new HashMap<>();
        cachedContent = null;
        bytes = new ByteArrayBuffer(PREV_BYTES_LENGTH);
        respConsumed = false;

        try {
            this.httpResp = resp.returnResponse();
            parseResponse(httpResp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String getContent() {
        if (!respConsumed) {
            try {
                cachedContent = EntityUtils.toString(httpResp.getEntity(), charset);
            } catch (IOException e) {
                // ignore
            } finally {
                respConsumed = true;
            }
        }

        if(cachedContent != null) {
            return cachedContent;
        }

        try {
            return new String(bytes.buffer(), charset);
        } catch (IOException e) {
            // ignore
            Log.err("Unsupported charset : " + charset);
            return null;
        }
    }

    @Override
    public InputStream getInputStream() {
        Tools.assert0(!respConsumed, "'response' has alread been consumed !");

        try {
            InputStream is = new BufferedInputStream(httpResp.getEntity().getContent());
            PREV_BYTES_LENGTH = is.available();
            return new StringCachedInputStream(is, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            respConsumed = true;
        }

        return null;
    }

    // ----------------- 辅助方法 -----------------------

    /**
     * 解析响应消息 [cookie, charset等等]
     *
     * @param resp 给定的响应结果
     * @return void
     * @author Jerry.X.He
     * @date 5/10/2017 8:04 PM
     * @since 1.0
     */
    private void parseResponse(HttpResponse resp) {
        Header[] headers = resp.getAllHeaders();

        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            String headerName = header.getName();
            if (Tools.equalsIgnoreCase(headerName, Tools.RESP_COOKIE_STR)) {
                String headerValue = header.getValue();
                Set<String> nonCookieKeysTmp = NON_COOKIE_KESY;
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
                this.headers.put(header.getName(), header.getValue());
            } else {
                this.headers.put(header.getName(), header.getValue());
            }
        }
    }

    /**
     * 获取响应头中的"Set-Cookie" 中对应的cookie
     * 以; 分割kv对, =分割key 和value
     * 这样解析不太好啊[鲁棒性],, 直接split就行了		add at 2016.05.02
     *
     * @param value         给定的cookie的值
     * @param nonCookieKeys 一些不属于cookie核心数据的cookie元数据
     * @return org.apache.http.NameValuePair[]
     * @author Jerry.X.He
     * @date 5/10/2017 8:05 PM
     * @since 1.0
     */
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

    /**
     * 获取统一的大写 或者小写
     *
     * @param str 给定的字符串
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/10/2017 8:06 PM
     * @since 1.0
     */
    private static String getStdForString(String str) {
        return str.toUpperCase();
    }

}
