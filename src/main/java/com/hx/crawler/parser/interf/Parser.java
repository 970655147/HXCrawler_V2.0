/**
 * file name : Parser.java
 * created at : 13:37:01 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;


import com.hx.json.JSONArray;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;

/**
 * 通过indexString 和给定的document 解析结果
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:22 PM
 */
public abstract class Parser {

    public Parser() {

    }

    /**
     * 根据当前文档, 以及xpath索引字符串, 取到需要拿到的结果
     *
     * @param root   文档的根节点
     * @param url    文档对应的url
     * @param idxStr xpath索引字符串
     * @return com.hx.json.JSONArray
     * @author 970655147 created at 2017-03-11 11:59
     */
    public abstract JSONArray parse(Element root, String url, String idxStr);

    /**
     * 根据当前文档, 以及xpath索引字符串, 取到需要拿到的结果
     *
     * @param html   当前文档的内容
     * @param url    文档对应的url
     * @param idxStr xpath索引字符串
     * @return com.hx.json.JSONArray
     * @author 970655147 created at 2017-03-11 11:59
     */
    public JSONArray parse(String html, String url, String idxStr) throws Exception {
        SAXReader saxReader = new SAXReader();
        Element root = saxReader.read(new StringReader(html)).getRootElement();
        return parse(root, url, idxStr);
    }

    /**
     * 解析target Endpoint
     * 先序遍历ep结点, 一次解析每一个结点的数据, 结果存放于res中
     *
     * @param root       root节点
     * @param currentEle 当前正在访问的元素 [对应一个ep]
     * @param url        正在爬取的url
     * @param ep         正在访问的endiPoint
     * @param res        收集结果的JSONArray
     * @param idx        当前正在处理的元素的索引
     * @return void
     * @author Jerry.X.He
     * @date 5/11/2017 8:45 PM
     * @since 1.0
     */
    public abstract void parse(Element root, Element currentEle, String url, Endpoint ep, JSONArray res, int idx);

}
