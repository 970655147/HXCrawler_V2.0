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
 * ���� xpath ������
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
     * ����target EndPoint
     * �������ep���, һ�ν���ÿһ����������, ��������res��
     *
     * @param root       root�ڵ�
     * @param currentEle ��ǰ���ڷ��ʵ�Ԫ�� [��Ӧһ��ep]
     * @param url        ������ȡ��url
     * @param ep         ���ڷ��ʵ�endiPoint
     * @param res        �ռ������JSONArray
     * @param idx        ��ǰ���ڴ����Ԫ�ص�����
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
     * ͨ�� xpath ץȡԪ��
     *
     * @param root       root�ڵ�
     * @param currentEle ��ǰ���ڴ���Ľڵ�
     * @param xPath      ������xpath
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
     * ͨ�� xpath ץȡ��һ��Ԫ��
     *
     * @param root       root�ڵ�
     * @param currentEle ��ǰ���ڴ���Ľڵ�
     * @param xPath      ������xpath
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
