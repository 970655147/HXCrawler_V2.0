/**
 * file name : EndPointHandler.java
 * created at : 2:02:38 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.xpathParser.interf;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Element;

// �����������Endpoint��ʱ��, �������߼�
public abstract class EndPointHandler {

	public abstract void handle(Element root, Element currentEle, String url, JSONArray res, int idx, EndPoint child, JSONObject curObj);
	
}
