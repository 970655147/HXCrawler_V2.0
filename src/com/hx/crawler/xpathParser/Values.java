/**
 * file name : Values.java
 * created at : 8:03:17 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.xpathParser;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.hx.crawler.xpathParser.interf.EndPoint;

// values Ԫ��
public final class Values extends EndPoint {
	
	// values����Ԫ��
	private List<EndPoint> childs;
	
	// ��ʼ��
	public Values(String name, String xpath, String handlerStr, EndPoint parent) {
		super(EndPoint.VALUES, name, xpath, handlerStr, parent);
		childs = new ArrayList<>();
	}
	
	// �����Ԫ��
	public void addChild(EndPoint endPoint) {
		childs.add(endPoint);
	}
	// ��ȡ��Ԫ��
	public EndPoint getChild(int idx) {
		return childs.get(idx);
	}
	
	// values �ĺ��Ӹ���Ϊchilds�Ľ����Ŀ[���õݹ�]
	public int childSize() {
		return childs.size();
	}
	
	// ��ȡattr [values ��֧�ִ˲���]
	public String getAttribute() {
		throw new RuntimeException("unsupported operation exception ...");
	}

	// for debug ...
	public String toString() {
		return new JSONObject().element("fromEndPoint", super.toString() ).element("childs", childs).toString();
	}
	
}
