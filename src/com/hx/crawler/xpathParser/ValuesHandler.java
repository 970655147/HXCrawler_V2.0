/**
 * file name : ValuesHandler.java
 * created at : 2:14:16 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.xpathParser;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Element;

import com.hx.crawler.util.HXCrawlerConstants;
import com.hx.crawler.xpathParser.interf.EndPoint;
import com.hx.crawler.xpathParser.interf.EndPointHandler;

//values 结点的相关业务处理
public class ValuesHandler extends EndPointHandler {

	@Override
	public void handle(Element root, Element currentEle, String url, JSONArray res, int idx, EndPoint child, JSONObject curObj) {
		if(! checkCompatible((Values) child) ) {
			throw new RuntimeException("the valuesNode : " + child.getName() + ", xpath : " + child.getXPath() + " is not compatible with current version of HXCrawler !");
		}
		JSONArray curArr = new JSONArray();
		List<Element> eles = XPathParser.getResultByXPath(root, currentEle, child.getXPath() );
		int idx2 = 0;
		for(Element ele : eles) {
			XPathParser.parse0(root, ele, url, child, curArr, idx2 ++);
		}
		
		curObj.element(child.getName(), curArr);
	}

	// 校验给定的Values的兼容性 [不能同时存在name为"ArrayAttribute" 和name为其他字符串的属性]
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
