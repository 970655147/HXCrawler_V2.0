/**
 * file name : AttributeHandler.java
 * created at : 2:07:08 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.xpathImpl.XPathParser;
import java.util.List;

import org.dom4j.Element;

import com.hx.crawler.util.CrawlerConstants;
import com.hx.crawler.parser.interf.EndPoint;
import com.hx.crawler.parser.interf.EndPointHandler;
import com.hx.log.util.Tools;

import com.hx.json.JSONArray;
import com.hx.json.JSONObject;

// attribute 结点的相关业务处理
public final class AttributeHandler extends EndPointHandler {

    @Override
    public void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
                       EndPoint child, JSONObject curObj) {
        // 普通attribute结点
        if (!child.getName().equals(CrawlerConstants.ARRAY_ATTR)) {
            child.getHandler().cleanImmediateReturnFlag();
            if (child.getXPath() != null) {
                Element ele = XPathParser.getSingleResultByXPath(root, currentEle, child.getXPath());
                String handledResult = handleResult(child, getValueByAttribute(ele, child.getAttribute(), url, 0));
                curObj.element(child.getName(), handledResult);
            } else {
                String handledResult = handleResult(child, getValueByAttribute(currentEle, child.getAttribute(), url, idx));
                curObj.element(child.getName(), handledResult);
            }
            // arrayAttribute结点
        } else {
            JSONArray curArr = new JSONArray();
            List<Element> eles = XPathParser.getResultByXPath(root, currentEle, child.getXPath());
            int idx2 = 0;
            for (Element ele : eles) {
                child.getHandler().cleanImmediateReturnFlag();
                String handledResult = handleResult(child, getValueByAttribute(ele, child.getAttribute(), url, idx2++));
                if (child.getHandler().immediateReturn()) {
                    child.getHandler().handleImmediateReturn();
                } else {
                    curArr.add(handledResult);
                }
            }
            res.add(curArr);
        }
    }

    // 使用当前AttriBute的相关handler处理结果
    private String handleResult(EndPoint child, String valueByAttribute) {
        return child.getHandler().handle(valueByAttribute);
    }

    /**
     * 通过element 和attribute属性 获取其对应的值
     *
     * @param ele       当前元素
     * @param attribute 属性的key, :index, innerText, innerHtml, outerHtml, href, ..
     * @param url       当前url
     * @param idx       当前元素在父级元素的索引
     * @return java.lang.String
     * @throws
     * @author 970655147 created at 2017-03-11 12:32
     */
    private static String getValueByAttribute(Element ele, String attribute, String url, int idx) {
        if (CrawlerConstants.INDEX.equals(attribute)) {
            return String.valueOf(idx);
        }
        if (ele == null) {
            return CrawlerConstants.NULL;
        }

        if (CrawlerConstants.TEXT.equals(attribute)) {
            return ele.getText();
        } else if (CrawlerConstants.INNER_TEXT.equals(attribute)) {
            StringBuilder sb = new StringBuilder();
            getInnerText(ele, sb);
            return sb.toString();
        } else if (CrawlerConstants.INNER_HTML.equals(attribute)) {
            StringBuilder sb = new StringBuilder();
            getInnerHtml(ele, sb);
            return sb.toString();
        } else if (CrawlerConstants.OUTER_HTML.equals(attribute)) {
            StringBuilder sb = new StringBuilder();
            getOuterHtml(ele, sb);
            return sb.toString();
        }

        String res = ele.attributeValue(attribute, CrawlerConstants.ATTR_NOT_SUPPORT);
        if ((!res.equals(CrawlerConstants.ATTR_NOT_SUPPORT)) && CrawlerConstants.ATTR_MAY_LINKS.contains(attribute)) {
            res = Tools.transformUrl(url, res);
        }
        return res;
    }

    // 获取ele的innertext [但是不能解决子标签左右两边的文字的位置关系的问题]
    // 后来使用getStringValue() 方法解决了上面的问题
    private static void getInnerText(Element ele, StringBuilder sb) {
        sb.append(ele.getStringValue());
    }

    // 获取ele的innerhtml
    private static void getInnerHtml(Element ele, StringBuilder sb) {
        String outerHtml = ele.asXML();
        int start = outerHtml.indexOf(">") + 1;
        int end = outerHtml.lastIndexOf("<");
        sb.append(outerHtml, start, end);
    }

    // 获取ele的outerhtml
    private static void getOuterHtml(Element ele, StringBuilder sb) {
        sb.append(ele.asXML());
    }
}
