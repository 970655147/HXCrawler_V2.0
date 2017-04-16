/**
 * file name : Parser.java
 * created at : 8:49:07 AM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.xpathImpl;

import java.util.Collections;
import java.util.List;

import com.hx.json.JSONArray;
import com.hx.json.JSONObject;

import org.dom4j.Element;

import com.hx.crawler.util.CrawlerConstants;
import com.hx.crawler.parser.interf.EndPoint;
import com.hx.crawler.parser.interf.Parser;

// xpath 解析器
public final class XPathParser extends Parser {
	
	// 以root为根节点, 解析xpath字符串[指定的格式]
    @Override
	public JSONArray parse(Element root, String url, String xpath) {
		JSONArray res = new JSONArray();
		XpathIndexString idxStr = new XpathIndexString(xpath);
		
		EndPoint rootEp = idxStr.getRoot();
		parse(root, root, url, rootEp, res, 0);
		
		return res;
	}

	// 解析target EndPoint
	// 先序遍历ep结点, 一次解析每一个结点的数据, 结果存放于res中
	public static void parse(Element root, Element currentEle, String url, EndPoint ep, JSONArray res, int idx) {
		JSONObject curObj = new JSONObject();
		boolean beFiltered = false;
		for(int i=0; i<ep.childSize(); i++) {
			EndPoint child = ep.getChild(i);
			CrawlerConstants.ENDPOINT_TO_HANDLER.get(child.getType() ).handle(root, currentEle, url, res, idx, child, curObj);
			if(! child.getName().equals(CrawlerConstants.ARRAY_ATTR) ) {
				if(child.getHandler().immediateReturn() ) {
					child.getHandler().handleImmediateReturn();
					beFiltered = true;
					break ;
				}
			}
		}
		if(! beFiltered) {
			if(curObj.size() > 0) {
				res.add(curObj);
			}
		}
	}

	// 通过xpath抓取元素
	public static List<Element> getResultByXPath(Element root, Element currentEle, String xPath) {
		if(xPath.startsWith("/")) {
			return root.selectNodes(xPath);
		} else if(xPath.startsWith(".")) {
			return currentEle.selectNodes(xPath);
		}
		
		return Collections.emptyList();
	}
	// 通过xpath抓取第一个元素
	public static Element getSingleResultByXPath(Element root, Element currentEle, String xPath) {
		if(xPath.startsWith("/")) {
			return (Element) root.selectSingleNode(xPath);
		} else if(xPath.startsWith(".")) {
			return (Element) currentEle.selectSingleNode(xPath);
		}
		
		return null;
	}
	
}
