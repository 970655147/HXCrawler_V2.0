/**
 * file name : ValuesHandler.java
 * created at : 2:14:16 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.interf.EndPoint;
import com.hx.crawler.parser.interf.EndPointHandler;
import com.hx.crawler.parser.xpathImpl.XPathParser;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;
import org.dom4j.Element;

import java.util.List;

/**
 * values 结点的相关业务处理
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:38 PM
 */
public final class ValuesHandler extends EndPointHandler {

    @Override
    public void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
                       EndPoint child, JSONObject curObj) {
        if (!checkCompatible((Values) child)) {
            throw new RuntimeException("the valuesNode : " + child.getName() + ", xpath : " + child.getXPath() +
                    " is not compatible with current version of HXCrawler !");
        }
        JSONArray curArr = new JSONArray();
        List<Element> eles = XPathParser.getResultByXPath(root, currentEle, child.getXPath());
        int idx2 = 0;
        for (Element ele : eles) {
            XPathParser.parse(root, ele, url, child, curArr, idx2++);
        }

        curObj.element(child.getName(), curArr);
    }

    /**
     * 校验给定的Values的兼容性 [不能同时存在name为"ArrayAttribute" 和name为其他字符串的属性]
     *
     * @param values 需要校验的 Values 节点
     * @return boolean
     * @author Jerry.X.He
     * @date 5/11/2017 8:38 PM
     * @since 1.0
     */
    private static boolean checkCompatible(Values values) {
        boolean isContainArrayAttribute = false, isContainOther = false;
        for (int i = 0; i < values.childSize(); i++) {
            if (values.getChild(i).getName().equals(CrawlerConstants.ARRAY_ATTR)) {
                isContainArrayAttribute = true;
            } else {
                isContainOther = true;
            }
        }

        return !(isContainArrayAttribute && isContainOther);
    }
}
