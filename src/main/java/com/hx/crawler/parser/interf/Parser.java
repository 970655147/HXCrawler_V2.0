/**
 * file name : Parser.java
 * created at : 13:37:01 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;


import java.io.StringReader;
import net.sf.json.JSONArray;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

// 通过indexString 和给定的document 解析结果
public abstract class Parser {

    /**
     * 根据当前文档, 以及xpath索引字符串, 取到需要拿到的结果
     *
     * @param root   文档的根节点
     * @param url    文档对应的url
     * @param idxStr xpath索引字符串
     * @return net.sf.json.JSONArray
     * @throws
     * @author 970655147 created at 2017-03-11 11:59
     */
    public abstract JSONArray parse(Element root, String url, String idxStr);

    /**
     * 根据当前文档, 以及xpath索引字符串, 取到需要拿到的结果
     *
     * @param html   当前文档的内容
     * @param url    文档对应的url
     * @param idxStr xpath索引字符串
     * @return net.sf.json.JSONArray
     * @throws
     * @author 970655147 created at 2017-03-11 11:59
     */
    public JSONArray parse(String html, String url, String idxStr) throws Exception {
        SAXReader saxReader = new SAXReader();
        Element root = saxReader.read(new StringReader(html)).getRootElement();
        return parse(root, url, idxStr);
    }

}
