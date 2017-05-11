/**
 * file name : Parser.java
 * created at : 8:49:07 AM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.xpathImpl;

import com.hx.crawler.parser.interf.EndPoint;
import com.hx.crawler.parser.interf.Parser;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;
import org.dom4j.Element;

import java.util.Collections;
import java.util.List;

/**
 * 基于 xpath 解析器
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:45 PM
 */
public final class XPathParser extends Parser {

    @Override
    public JSONArray parse(Element root, String url, String xpath) {
        JSONArray res = new JSONArray();
        XpathIndexString idxStr = new XpathIndexString(xpath);

        EndPoint rootEp = idxStr.getRoot();
        parse(root, root, url, rootEp, res, 0);

        return res;
    }

    /**
     * 解析target EndPoint
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
    public static void parse(Element root, Element currentEle, String url, EndPoint ep, JSONArray res, int idx) {
        JSONObject curObj = new JSONObject();
        boolean beFiltered = false;
        for (int i = 0; i < ep.childSize(); i++) {
            EndPoint child = ep.getChild(i);
            CrawlerConstants.ENDPOINT_TO_HANDLER.get(child.getType()).handle(root, currentEle, url, res, idx, child, curObj);
            if (!child.getName().equals(CrawlerConstants.ARRAY_ATTR)) {
                if (child.getHandler().immediateReturn()) {
                    child.getHandler().handleImmediateReturn();
                    beFiltered = true;
                    break;
                }
            }
        }

        if (!beFiltered) {
            if (curObj.size() > 0) {
                res.add(curObj);
            }
        }
    }

    /**
     * 通过 xpath 抓取元素
     *
     * @param root       root节点
     * @param currentEle 当前正在处理的节点
     * @param xPath      给定的xpath
     * @return java.util.List<org.dom4j.Element>
     * @author Jerry.X.He
     * @date 5/11/2017 8:47 PM
     * @since 1.0
     */
    public static List<Element> getResultByXPath(Element root, Element currentEle, String xPath) {
        if (xPath.startsWith("/")) {
            return root.selectNodes(xPath);
        } else if (xPath.startsWith(".")) {
            return currentEle.selectNodes(xPath);
        }

        return Collections.emptyList();
    }

    /**
     * 通过 xpath 抓取第一个元素
     *
     * @param root       root节点
     * @param currentEle 当前正在处理的节点
     * @param xPath      给定的xpath
     * @return org.dom4j.Element
     * @author Jerry.X.He
     * @date 5/11/2017 8:47 PM
     * @since 1.0
     */
    public static Element getSingleResultByXPath(Element root, Element currentEle, String xPath) {
        if (xPath.startsWith("/")) {
            return (Element) root.selectSingleNode(xPath);
        } else if (xPath.startsWith(".")) {
            return (Element) currentEle.selectSingleNode(xPath);
        }

        return null;
    }

}
