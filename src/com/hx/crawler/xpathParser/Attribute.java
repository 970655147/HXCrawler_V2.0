/**
 * file name : Attribute.java
 * created at : 7:57:53 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.xpathParser;



import net.sf.json.JSONObject;

import com.hx.crawler.xpathParser.interf.EndPoint;

// attribute���
public final class Attribute extends EndPoint {
	
	// attribute[text, innertext, innerhtml, outerhtml]
	private String attr;
	
	// ��ʼ��
	public Attribute(String name, String xpath, String attr, String handlerStr, EndPoint parent) {
		super(EndPoint.ATTRIBUTE, name, xpath, handlerStr, parent);
		this.attr = attr;
	}

	// �����Ԫ��[attribute ��֧������ӽڵ�]
	public void addChild(EndPoint endPoint) {
		throw new RuntimeException("unsupported operation exception ...");
	}
	
	// ��ȡ��Ԫ��[attribute û����Ԫ��]
	public EndPoint getChild(int idx) {
		throw new RuntimeException("unsupported operation exception ...");
	}
	
	// for debug ...
	public String toString() {
		return new JSONObject().element("fromEndPoint", super.toString() ).element("attribute", attr).toString();
	}

	// Attribute���ĺ��Ӹ���Ϊ0
	public int childSize() {
		return 0;
	}
	
	// ��ȡattr
	public String getAttribute() {
		return attr;
	}

}
