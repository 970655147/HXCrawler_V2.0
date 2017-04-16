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

import com.hx.attr_handler.attr_handler.DoNothingAttrHandler;
import com.hx.attr_handler.attr_handler.StandardHandlerParser.Types;
import com.hx.attr_handler.attr_handler.operation.MapOperationAttrHandler;
import com.hx.attr_handler.attr_handler.operation.interf.OperationAttrHandler;
import com.hx.crawler.parser.AttributeHandler;
import com.hx.crawler.parser.ValuesHandler;
import com.hx.crawler.parser.interf.EndPointHandler;
import com.hx.log.util.Tools;

// 常量
public final class CrawlerConstants {
	
	// disable constructor
	private CrawlerConstants() {
		Tools.assert0("can't instantiate !");
	}
	
	// the key may occur in 'xpathTemplate'
	/**
	 * 'name', represents the key in result of currentNode, ARRAY_ATTR is special, it without 'name'
	 * 'xpath', represents the search path[in grammar of xpath], of current node
	 * 'values', represents the current node's structure of childNodes [ValueNode only]
	 * 'attribute', represents the way to got current node's value [AttributeNode only]
	 * 'hanler', represents the handler to handle the result of currentNode[AttributeNode only]
	 */
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
	
	// the 'attr' maybe a 'hyperLink', some time may need recouple absolute url
	public final static Set<String> ATTR_MAY_LINKS = new HashSet<>();
	static {
		ATTR_MAY_LINKS.add("href");
		ATTR_MAY_LINKS.add("src");
	}
	
	// {'attribute' : AttributeHandler, ... }
	public final static Map<String, EndPointHandler> ENDPOINT_TO_HANDLER = new HashMap<>();
	public final static OperationAttrHandler DEFAULT_OPERATION_ATTR_HANDLER = new MapOperationAttrHandler(new DoNothingAttrHandler(), Types.String);
	
	static {
		ENDPOINT_TO_HANDLER.put(ATTRIBUTE, new AttributeHandler() );
		ENDPOINT_TO_HANDLER.put(VALUES, new ValuesHandler() );
		// 不能向下面 这样写, 因为是EndPoint.ATTRIBUTE的初始化导致了Constants.class的加载, 而执行当前staticBlock的时候, Endpoint.ATTRIBUTE 以及EndPoint.VALUES在初始化阶段初始化的null值		--2016.02.02
//		ENDPOINT_TO_HANDLER.put(EndPoint.ATTRIBUTE, new ValuesHandler() );
	}

}
