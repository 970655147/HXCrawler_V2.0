package com.hx.crawler.parser.interf;

import com.hx.json.JSONArray;

/**
 * 抓取数据之后的Context
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:23 PM
 */
public interface ResultContext {

    /**
     * 获取当前使用的xpath的索引
     *
     * @return the idx of  xpath
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    int xpathIdx();

    /**
     * 获取当前使用的xpath
     *
     * @return the xpath use now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    String xpath();

    /**
     * 获取当前抓取的 url
     *
     * @return the url crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    String url();

    /**
     * 获取当前抓取的html
     *
     * @return the html crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    String html();

    /**
     * 获取当前抓取的结果
     *
     * @return the result crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    JSONArray fetchedData();

    /**
     * 获取当前抓取数据的parser
     *
     * @return the parser crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    Parser parser();

}
