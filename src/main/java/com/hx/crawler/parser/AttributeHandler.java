/**
 * file name : AttributeHandler.java
 * created at : 2:07:08 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.interf.EndPoint;
import com.hx.crawler.parser.interf.EndPointHandler;
import com.hx.crawler.parser.xpathImpl.XPathParser;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;
import com.hx.log.util.Tools;
import org.dom4j.Element;

import java.util.List;

/**
 * attribute �������ҵ����
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:34 PM
 */
public final class AttributeHandler extends EndPointHandler {

    @Override
    public void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
                       EndPoint child, JSONObject curObj) {
        // ��ͨattribute���
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
        // arrayAttribute���
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

    /**
     * ʹ�õ�ǰ AttriBute ����� handler ������
     *
     * @param child            ��������Ԫ��
     * @param valueByAttribute ��Ԫ�ص�ֵ
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/11/2017 8:36 PM
     * @since 1.0
     */
    private String handleResult(EndPoint child, String valueByAttribute) {
        return child.getHandler().handle(valueByAttribute);
    }

    /**
     * ͨ��element ��attribute���� ��ȡ���Ӧ��ֵ
     *
     * @param ele       ��ǰԪ��
     * @param attribute ���Ե�key, :index, innerText, innerHtml, outerHtml, href, ..
     * @param url       ��ǰurl
     * @param idx       ��ǰԪ���ڸ���Ԫ�ص�����
     * @return java.lang.String
     * @author 970655147 created at 2017-03-11 12:32
     */
    private static String getValueByAttribute(Element ele, String attribute, String url, int idx) {
        if (CrawlerConstants.INDEX.equals(attribute)) {
            return String.valueOf(idx);
        }
        if (ele == null) {
            return Tools.NULL;
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

    /**
     * ��ȡele��innertext [���ǲ��ܽ���ӱ�ǩ�������ߵ����ֵ�λ�ù�ϵ������]
     * ����ʹ��getStringValue() ������������������
     *
     * @param ele �����Ľڵ�Ԫ��
     * @param sb  �ռ����ݵ�sb
     * @return void
     * @author Jerry.X.He
     * @date 5/11/2017 8:34 PM
     * @since 1.0
     */
    private static void getInnerText(Element ele, StringBuilder sb) {
        sb.append(ele.getStringValue());
    }

    /**
     * ��ȡ ele �� innerhtml
     *
     * @param ele �����Ľڵ�Ԫ��
     * @param sb  �ռ����ݵ�sb
     * @return void
     * @author Jerry.X.He
     * @date 5/11/2017 8:35 PM
     * @since 1.0
     */
    private static void getInnerHtml(Element ele, StringBuilder sb) {
        String outerHtml = ele.asXML();
        int start = outerHtml.indexOf(">") + 1;
        int end = outerHtml.lastIndexOf("<");
        sb.append(outerHtml, start, end);
    }

    /**
     * ��ȡ ele �� outerhtml
     *
     * @param ele �����Ľڵ�Ԫ��
     * @param sb  �ռ����ݵ�sb
     * @return void
     * @author Jerry.X.He
     * @date 5/11/2017 8:35 PM
     * @since 1.0
     */
    private static void getOuterHtml(Element ele, StringBuilder sb) {
        sb.append(ele.asXML());
    }
}
