/**
 * file name : Parser.java
 * created at : 8:49:07 AM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.xpathParser;

import java.io.StringReader;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hx.crawler.util.Constants;
import com.hx.crawler.xpathParser.interf.EndPoint;
import com.hx.crawler.xpathParser.interf.Parser;
import com.hx.crawler.xpathParser.interf.Parser;

public final class XPathParser extends Parser {
	
	// ��rootΪ���ڵ�, ����xpath�ַ���[ָ���ĸ�ʽ]
	public JSONArray parse(Element root, String url, String xpath) {
		JSONArray res = new JSONArray();
		XpathIndexString idxStr = new XpathIndexString(xpath);
		
		EndPoint rootEp = idxStr.getRoot();
		parse0(root, root, url, rootEp, res, 0);
		
		return res;
	}
	public JSONArray parse(String html, String url, String xpath) throws Exception {
		SAXReader saxReader = new SAXReader();
		Element root = saxReader.read(new StringReader(html)).getRootElement();
		return parse(root, url, xpath);
	}

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
