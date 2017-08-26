/**
 * file name : Parser.java
 * created at : 8:49:07 AM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.xpathImpl;

import com.hx.crawler.parser.interf.Endpoint;
import com.hx.crawler.parser.interf.EndpointHandler;
import com.hx.crawler.parser.interf.Parser;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;
import com.hx.log.util.Tools;
import org.dom4j.Element;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 基于 xpath 解析器
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:45 PM
 */
public final class XPathParser extends Parser {

    // ----------------- 工具方法 -----------------------

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

    // ----------------- 实例部分 -----------------------

    /**
     * endpoint -> endpointHandler
     */
    private Map<String, EndpointHandler> endpoint2Handler;

    /**
     * 初始化
     * 创建root 结点, 并解析
     *
     * @param endpoint2Handler endpoint -> endpointHandler
     * @since 1.0
     */
    public XPathParser(Map<String, EndpointHandler> endpoint2Handler) {
        this.endpoint2Handler = endpoint2Handler;
        preCheck();
    }

    public XPathParser() {
        this(CrawlerConstants.ENDPOINT_TO_HANDLER);
    }

    @Override
    public JSONArray parse(Element root, String url, String xpath) {
        JSONArray res = new JSONArray();
        XpathIndexString idxStr = new XpathIndexString(xpath);

        Endpoint rootEp = idxStr.getRoot();
        parse(root, root, url, rootEp, res, 0);

        return res;
    }

    @Override
    public void parse(Element root, Element currentEle, String url, Endpoint ep, JSONArray res, int idx) {
        JSONObject curObj = new JSONObject();
        boolean beFiltered = false;
        for (int i = 0; i < ep.childSize(); i++) {
            Endpoint child = ep.getChild(i);
            EndpointHandler handler = endpoint2Handler.get(child.getType().type());
            handler.handle(root, currentEle, url, res, idx, child, curObj);
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

    // ----------------- 辅助方法 -----------------------

    /**
     * 校验当前 XpathIndexString 的相关配置
     *
     * @return void
     * @author Jerry.X.He
     * @date 8/26/2017 7:27 PM
     * @since 1.0
     */
    private void preCheck() {
        for (Map.Entry<String, EndpointHandler> entry : CrawlerConstants.ENDPOINT_TO_HANDLER.entrySet()) {
            String key = entry.getKey();
            EndpointHandler handler = endpoint2Handler.get(key);
            Tools.assert0(handler != null, "the handler of '" + key + "' is null, please register it ! ");
        }
    }

}
