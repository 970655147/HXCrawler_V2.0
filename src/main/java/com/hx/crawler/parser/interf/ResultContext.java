package com.hx.crawler.parser.interf;

import com.hx.json.JSONArray;

/**
 * ץȡ����֮���Context
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:23 PM
 */
public interface ResultContext {

    /**
     * ��ȡ��ǰʹ�õ�xpath������
     *
     * @return the idx of  xpath
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    int xpathIdx();

    /**
     * ��ȡ��ǰʹ�õ�xpath
     *
     * @return the xpath use now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    String xpath();

    /**
     * ��ȡ��ǰץȡ�� url
     *
     * @return the url crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    String url();

    /**
     * ��ȡ��ǰץȡ��html
     *
     * @return the html crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    String html();

    /**
     * ��ȡ��ǰץȡ�Ľ��
     *
     * @return the result crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    JSONArray fetchedData();

    /**
     * ��ȡ��ǰץȡ���ݵ�parser
     *
     * @return the parser crawl now
     * @author Jerry.X.He
     * @date 5/11/2017 8:25 PM
     * @since 1.0
     */
    Parser parser();

}
