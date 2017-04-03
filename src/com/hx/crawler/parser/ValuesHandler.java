/**
 * file name : ValuesHandler.java
 * created at : 2:14:16 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.xpathImpl.XPathParser;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Element;

import com.hx.crawler.util.HXCrawlerConstants;
import com.hx.crawler.parser.interf.EndPoint;
import com.hx.crawler.parser.interf.EndPointHandler;

//values �������ҵ����
public final class ValuesHandler extends EndPointHandler {

	@Override
	public void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
					   EndPoint child, JSONObject curObj) {
		if(! checkCompatible((Values) child) ) {
			throw new RuntimeException("the valuesNode : " + child.getName() + ", xpath : " + child.getXPath() + " is not compatible with current version of HXCrawler !");
		}
		JSONArray curArr = new JSONArray();
		List<Element> eles = XPathParser.getResultByXPath(root, currentEle, child.getXPath() );
		int idx2 = 0;
		for(Element ele : eles) {
			XPathParser.parse(root, ele, url, child, curArr, idx2 ++);
		}
		
		curObj.element(child.getName(), curArr);
	}

	// У�������Values�ļ����� [����ͬʱ����nameΪ"ArrayAttribute" ��nameΪ�����ַ���������]
	private static boolean checkCompatible(Values values) {
		boolean isContainArrayAttribute = false, isContainOther = false;
		for(int i=0; i<values.childSize(); i++) {
			if(values.getChild(i).getName().equals(HXCrawlerConstants.ARRAY_ATTR) ) {
				isContainArrayAttribute = true;
			} else {
				isContainOther = true;
			}
		}
		
		return ! (isContainArrayAttribute && isContainOther);
	}
}
