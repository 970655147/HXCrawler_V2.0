/**
 * file name : Constants.java
 * created at : 8:06:27 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hx.attrHandler.attrHandler.DoNothingAttrHandler;
import com.hx.attrHandler.attrHandler.StandardHandlerParser.Types;
import com.hx.attrHandler.attrHandler.operation.MapOperationAttrHandler;
import com.hx.attrHandler.attrHandler.operation.interf.OperationAttrHandler;
import com.hx.crawler.xpathParser.AttributeHandler;
import com.hx.crawler.xpathParser.ValuesHandler;
import com.hx.crawler.xpathParser.interf.EndPointHandler;

// 常量
public class Constants {
	
	// the key may occur in 'xpathTemplate'
	public final static String NAME = "name";
	public final static String XPATH = "xpath";
	public final static String VALUES = "values";
	public final static String ATTRIBUTE = "attribute";
	public final static String HANDLER = "handler";
	
	// handler actionMode
	// '+' represents 'add' handler based on 'parentNode'
	// '-' represents 'override handler', discard all handler of 'parentNode'
	public final static String HANDLER_ADDED = "+";
	public final static String HANDLER_OVERRIDE = "-";
	
	// the special value may be 'AttributeNode''s value
	// ':idex', repsresents got current 'resultNode' index in 'parentNode'[ValueNode]
	// 'text', respresents got 'selectedNode''s text[not include subTag, and subTag's text]
	// 'innertext', respresents got 'selectedNode''s innerText[not include subTag, include subTag's text]
	// 'innerhtml', respresents got 'selectedNode''s innerHTML[include subTag, include subTag's text]
	// 'outerhtml', based on 'innerhtml' and add 'currentTag' outside
	// 'attrName', represents got 'selectedNode''s 'attr(attrName)'[got defined 'attr']
	public final static String INDEX = ":index";
	public final static String TEXT = "text";
	public final static String INNER_TEXT = "innertext";
	public final static String INNER_HTML = "innerhtml";
	public final static String OUTER_HTML = "outerhtml";
	
	// Constants
	public static final String EMPTY_STR = "";
	public static final String CRLF = "\r\n";
	public final static String NULL = "null";
	public final static String DEFAULT_VALUE = NULL;
	public final static String ATTR_NOT_SUPPORT = "AttributeNotSupported";
	public final static String ARRAY_ATTR = "JSONArrayAttribute";
	
	// Constants
	public final static Character SLASH = '\\';
	public final static Character INV_SLASH = '/';
	public static final Character QUESTION = '?';
	public static final Character DOT = '.';
	public static final Character COMMA = ',';
	public static final Character COLON = ':';	
	public static final Character SPACE = ' ';
	public static final Character TAB = '\t';
	public static final Character CR = '\r';
	public static final Character LF = '\n';
	public static final Character QUOTE = '\"';
	public static final Character SINGLE_QUOTE = '\'';
	
	// 'rootNode' int result of 'XpathIndexString'
	public final static String ROOT = "#root";
	
	// the 'attr' maybe a 'hyperLink'
	public final static Set<String> links = new HashSet<>();
	static {
		links.add("href");
		links.add("src");
	}
	
	// {'attribute' : AttributeHandler, ... }
	public final static Map<String, EndPointHandler> endpointToHandler = new HashMap<>();
	public final static OperationAttrHandler defaultOperationAttrHandler = new MapOperationAttrHandler(new DoNothingAttrHandler(), Types.String);
	
	static {
		endpointToHandler.put(ATTRIBUTE, new AttributeHandler() );
		endpointToHandler.put(VALUES, new ValuesHandler() );
		// 不能向下面 这样写, 因为是EndPoint.ATTRIBUTE的初始化导致了Constants.class的加载, 而执行当前staticBlock的时候, Endpoint.ATTRIBUTE 以及EndPoint.VALUES在初始化阶段初始化的null值		--2016.02.02
//		endpointToHandler.put(EndPoint.ATTRIBUTE, new ValuesHandler() );
	}

}
