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
     * 上下文中使用的xpath的索引
     */
    private int xpathIdx;
    /**
     * 上下文中使用的xpath
     */
    private String xpath;
    /**
     * 上下文中使用的 url
     */
    private String url;
    /**
     * 上下文中使用的 html
     */
    private String html;
    /**
     * 抓取的结果
     */
    private JSONArray fetchedData;
    /**
     * 抓取的结果使用的 parser
     */
    private Parser parser;

    /**
     * 初始化
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
