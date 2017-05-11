package com.hx.crawler.parser;

import com.hx.crawler.parser.interf.Parser;
import com.hx.crawler.parser.interf.ResultContext;
import com.hx.json.JSONArray;

/**
 * SimpleResultContext
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:28 PM
 */
public class SimpleResultContext implements ResultContext {

    /**
     * ��������ʹ�õ�xpath������
     */
    private int xpathIdx;
    /**
     * ��������ʹ�õ�xpath
     */
    private String xpath;
    /**
     * ��������ʹ�õ� url
     */
    private String url;
    /**
     * ��������ʹ�õ� html
     */
    private String html;
    /**
     * ץȡ�Ľ��
     */
    private JSONArray fetchedData;
    /**
     * ץȡ�Ľ��ʹ�õ� parser
     */
    private Parser parser;

    /**
     * ��ʼ��
     *
     * @param xpathIdx    xpathIdx
     * @param xpath       xpath
     * @param url         url
     * @param html        html
     * @param fetchedData fetchedData
     * @param parser      parser
     * @since 1.0
     */
    public SimpleResultContext(int xpathIdx, String xpath, String url, String html,
                               JSONArray fetchedData, Parser parser) {
        this.xpathIdx = xpathIdx;
        this.xpath = xpath;
        this.url = url;
        this.html = html;
        this.fetchedData = fetchedData;
        this.parser = parser;
    }

    @Override
    public int xpathIdx() {
        return xpathIdx;
    }

    @Override
    public String xpath() {
        return xpath;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String html() {
        return html;
    }

    @Override
    public JSONArray fetchedData() {
        return fetchedData;
    }

    @Override
    public Parser parser() {
        return parser;
    }
}
