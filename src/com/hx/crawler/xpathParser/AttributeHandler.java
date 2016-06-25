/**
 * file name : AttributeHandler.java
 * created at : 2:07:08 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.xpathParser;


import java.util.List;

import org.dom4j.Element;

import com.hx.crawler.util.CrawlerConstants;
import com.hx.crawler.xpathParser.interf.EndPoint;
import com.hx.crawler.xpathParser.interf.EndPointHandler;
import com.hx.log.util.Tools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

// attribute 结点的相关业务处理
public class AttributeHandler extends EndPointHandler {

	@Override
	public void handle(Element root, Element currentEle, String url, JSONArray res, int idx, EndPoint child, JSONObject curObj) {
		if(! child.getName().equals(CrawlerConstants.ARRAY_ATTR) ) {
			child.getHandler().cleanImmediateReturnFlag();
			if(child.getXPath() != null ) {
//				List<Element> eles = getResultByXPath(root, currentEle, child.getXPath());
//				int idx2 = 0;
				
				// 统一只获取第一个元素的数据
//				if(eles.size() == 1) {
//					curObj.element(child.getName(), getValueByAttribute(eles.get(0), child.getAttribute(), idx2) );
//				} else {
//					JSONArray attrVals = new JSONArray();
//					for(Element ele : eles) {
//						attrVals.add(getValueByAttribute(ele, child.getAttribute(), (idx2 ++)) );
//					}
//					curObj.element(child.getName(), attrVals);
//				}
				
				Element ele = XPathParser.getSingleResultByXPath(root, currentEle, child.getXPath());
				String handledResult = handleResult(child, getValueByAttribute(ele, child.getAttribute(), url, 0) );
				curObj.element(child.getName(), handledResult );
			} else {
				String handledResult = handleResult(child, getValueByAttribute(currentEle, child.getAttribute(), url, idx) );
				curObj.element(child.getName(), handledResult );
			}
		} else {
			JSONArray curArr = new JSONArray();
			List<Element> eles = XPathParser.getResultByXPath(root, currentEle, child.getXPath() );
			int idx2 = 0;
			for(Element ele : eles) {
				child.getHandler().cleanImmediateReturnFlag();
				String handledResult = handleResult(child, getValueByAttribute(ele, child.getAttribute(), url, idx2 ++) );
				if(child.getHandler().immediateReturn() ) {
					child.getHandler().handleImmediateReturn();
				} else {
					curArr.add(handledResult );
				}
			}
			res.add(curArr);
		}
	}

	// 使用当前AttriBute的相关handler处理结果
	private String handleResult(EndPoint child, String valueByAttribute) {
		return child.getHandler().handle(valueByAttribute);
 	}
	// 通过element 和attribute属性 获取其对应的值
	private static String getValueByAttribute(Element ele, String attribute, String url, int idx) {
		if(CrawlerConstants.INDEX.equals(attribute)) {
			return String.valueOf(idx);
		}
		if(ele == null) {
			return CrawlerConstants.NULL;
		}
		if(CrawlerConstants.TEXT.equals(attribute)) {
			return ele.getText();
		} else if(CrawlerConstants.INNER_TEXT.equals(attribute)) {
			StringBuilder sb = new StringBuilder();
			getInnerText(ele, sb);
			return sb.toString();
		} else if(CrawlerConstants.INNER_HTML.equals(attribute)) {
			StringBuilder sb = new StringBuilder();
			getInnerHtml(ele, sb);
			return sb.toString();
		} else if(CrawlerConstants.OUTER_HTML.equals(attribute)) {
			StringBuilder sb = new StringBuilder();
			getOuterHtml(ele, sb);
			return sb.toString();
		}
		
		String res = ele.attributeValue(attribute, CrawlerConstants.ATTR_NOT_SUPPORT);
		if((! res.equals(CrawlerConstants.ATTR_NOT_SUPPORT)) && CrawlerConstants.links.contains(attribute) ) {
			res = Tools.transformUrl(url, res);
		}
		return res;
	}

	// 获取ele的innertext [但是不能解决子标签左右两边的文字的位置关系的问题]
	// 后来使用getStringValue() 方法解决了上面的问题
	private static void getInnerText(Element ele, StringBuilder sb) {
		sb.append(ele.getStringValue());
		
//		sb.append(ele.getText());
//		Iterator<Node> it = ele.selectNodes("./*").iterator();
//		while(it.hasNext()) {
//			getInnerText((Element) it.next(), sb);
//		}
	}

	// 获取ele的innerhtml
	private static void getInnerHtml(Element ele, StringBuilder sb) {
		String outerHtml = ele.asXML();
		int start = outerHtml.indexOf(">") + 1;
		int end = outerHtml.lastIndexOf("<");
		sb.append(outerHtml, start, end);
		
//		sb.append(ele.getText());
//		Iterator<Node> it = ele.selectNodes("./*").iterator();
//		while(it.hasNext()) {
//			getInnerText(it.next(), sb);
//		}
	}

	// 获取ele的outerhtml
	private static void getOuterHtml(Element ele, StringBuilder sb) {
		sb.append(ele.asXML());
		
//		sb.append(ele.getText());
//		Iterator<Node> it = ele.selectNodes("./*").iterator();
//		while(it.hasNext()) {
//			getInnerText(it.next(), sb);
//		}
	}
}
