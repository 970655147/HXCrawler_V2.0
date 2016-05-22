/**
 * file name : Parser.java
 * created at : 13:37:01 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.xpathParser.interf;

import java.io.StringReader;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.hx.crawler.util.Constants;
import com.hx.crawler.xpathParser.XpathIndexString;
import com.hx.crawler.xpathParser.interf.EndPoint;

public abstract class Parser {
	
	// ��rootΪ���ڵ�, ����xpath�ַ���[ָ���ĸ�ʽ]
	public abstract JSONArray parse(Element root, String url, String idxStr);
	public abstract JSONArray parse(String html, String url, String idxStr) throws Exception;

	// ����target EndPoint
	// �������ep���, һ�ν���ÿһ����������, ��������res��
	static void parse0(Element root, Element currentEle, String url, EndPoint ep, JSONArray res, int idx) {
		JSONObject curObj = new JSONObject();
		boolean beFiltered = false;
		for(int i=0; i<ep.childSize(); i++) {
			EndPoint child = ep.getChild(i);
			Constants.endpointToHandler.get(child.getType() ).handle(root, currentEle, url, res, idx, child, curObj);
			if(! child.getName().equals(Constants.ARRAY_ATTR) ) {
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

	// ͨ��xpathץȡԪ��
	public static List<Element> getResultByXPath(Element root, Element currentEle, String xPath) {
		if(xPath.startsWith("/")) {
			return root.selectNodes(xPath);
		} else if(xPath.startsWith(".")) {
			return currentEle.selectNodes(xPath);
		}
		
		return null;
	}
	// ͨ��xpathץȡ��һ��Ԫ��
	public static Element getSingleResultByXPath(Element root, Element currentEle, String xPath) {
		if(xPath.startsWith("/")) {
			return (Element) root.selectSingleNode(xPath);
		} else if(xPath.startsWith(".")) {
			return (Element) currentEle.selectSingleNode(xPath);
		}
		
		return null;
	}
	
}
