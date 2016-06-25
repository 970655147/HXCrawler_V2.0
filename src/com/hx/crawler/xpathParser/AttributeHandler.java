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

// attribute �������ҵ����
public class AttributeHandler extends EndPointHandler {

	@Override
	public void handle(Element root, Element currentEle, String url, JSONArray res, int idx, EndPoint child, JSONObject curObj) {
		if(! child.getName().equals(CrawlerConstants.ARRAY_ATTR) ) {
			child.getHandler().cleanImmediateReturnFlag();
			if(child.getXPath() != null ) {
//				List<Element> eles = getResultByXPath(root, currentEle, child.getXPath());
//				int idx2 = 0;
				
				// ͳһֻ��ȡ��һ��Ԫ�ص�����
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

	// ʹ�õ�ǰAttriBute�����handler������
	private String handleResult(EndPoint child, String valueByAttribute) {
		return child.getHandler().handle(valueByAttribute);
 	}
	// ͨ��element ��attribute���� ��ȡ���Ӧ��ֵ
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

	// ��ȡele��innertext [���ǲ��ܽ���ӱ�ǩ�������ߵ����ֵ�λ�ù�ϵ������]
	// ����ʹ��getStringValue() ������������������
	private static void getInnerText(Element ele, StringBuilder sb) {
		sb.append(ele.getStringValue());
		
//		sb.append(ele.getText());
//		Iterator<Node> it = ele.selectNodes("./*").iterator();
//		while(it.hasNext()) {
//			getInnerText((Element) it.next(), sb);
//		}
	}

	// ��ȡele��innerhtml
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

	// ��ȡele��outerhtml
	private static void getOuterHtml(Element ele, StringBuilder sb) {
		sb.append(ele.asXML());
		
//		sb.append(ele.getText());
//		Iterator<Node> it = ele.selectNodes("./*").iterator();
//		while(it.hasNext()) {
//			getInnerText(it.next(), sb);
//		}
	}
}
