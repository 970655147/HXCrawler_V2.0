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

import com.hx.crawler.util.HXCrawlerConstants;
import com.hx.crawler.xpathParser.XpathIndexString;
import com.hx.crawler.xpathParser.interf.EndPoint;

public abstract class Parser {
	
	// 以root为根节点, 解析xpath字符串[指定的格式]
	public abstract JSONArray parse(Element root, String url, String idxStr);
	public abstract JSONArray parse(String html, String url, String idxStr) throws Exception;
	
}
