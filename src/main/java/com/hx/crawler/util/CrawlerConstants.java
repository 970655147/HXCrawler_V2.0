/**
 * file name : Constants.java
 * created at : 8:06:27 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.util;

import com.hx.attr_handler.attr_handler.DoNothingAttrHandler;
import com.hx.attr_handler.attr_handler.StandardHandlerParser.Types;
import com.hx.attr_handler.attr_handler.operation.MapOperationAttrHandler;
import com.hx.attr_handler.attr_handler.operation.interf.OperationAttrHandler;
import com.hx.crawler.parser.AttributeHandler;
import com.hx.crawler.parser.ValuesHandler;
import com.hx.crawler.parser.interf.EndPointHandler;
import com.hx.log.util.Tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Crawler ��صĳ���
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:48 PM
 */
public final class CrawlerConstants {

    // disable constructor
    private CrawlerConstants() {
        Tools.assert0("can't instantiate !");
    }

    // the key may occur in 'xpathTemplate'
    /**
     * 'name', represents the key in result of currentNode, ARRAY_ATTR is special, it without 'name'
     */
    public final static String NAME = "name";
    /**
     * 'xpath', represents the search path[in grammar of xpath], of current node
     */
    public final static String XPATH = "xpath";
    /**
     * 'values', represents the current node's structure of childNodes [ValueNode only]
     */
    public final static String VALUES = "values";
    /**
     * 'attribute', represents the way to got current node's value [AttributeNode only]
     */
    public final static String ATTRIBUTE = "attribute";
    /**
     * 'hanler', represents the handler to handle the result of currentNode[AttributeNode only]
     */
    public final static String HANDLER = "handler";

    // handler actionMode
    /**
     * '+' represents 'add' handler based on 'parentNode'
     */
    public final static String HANDLER_ADDED = "+";
    /**
     * '-' represents 'override handler', discard all handler of 'parentNode'
     */
    public final static String HANDLER_OVERRIDE = "-";

    // the special value may be 'AttributeNode''s value
    /**
     * ':idex', repsresents got current 'resultNode' index in 'parentNode'[ValueNode]
     */
    public final static String INDEX = ":index";
    /**
     * 'text', respresents got 'selectedNode''s text[not include subTag, and subTag's text]
     */
    public final static String TEXT = "text";
    /**
     * 'innertext', respresents got 'selectedNode''s innerText[not include subTag, include subTag's text]
     */
    public final static String INNER_TEXT = "innertext";
    /**
     * 'innerhtml', respresents got 'selectedNode''s innerHTML[include subTag, include subTag's text]
     */
    public final static String INNER_HTML = "innerhtml";
    /**
     * 'outerhtml', based on 'innerhtml' and add 'currentTag' outside
     */
    public final static String OUTER_HTML = "outerhtml";
    /**
     * other 'attrName', represents got 'selectedNode''s 'attr(attrName)'[got defined 'attr']
     */

    /**
     * ֧�� xpathParser ����س���
     */
    public final static String ATTR_NOT_SUPPORT = "AttributeNotSupported";
    public final static String ARRAY_ATTR = "JSONArrayAttribute";

    /**
     * 'rootNode' int result of 'XpathIndexString'
     */
    public final static String ROOT = "#root";

    /**
     * the 'attr' maybe a 'hyperLink', some time may need recouple absolute url
     */
    public final static Set<String> ATTR_MAY_LINKS = new HashSet<>();

    static {
        ATTR_MAY_LINKS.add("href");
        ATTR_MAY_LINKS.add("src");
    }

    /**
     * {'attribute' : AttributeHandler, ... }
     */
    public final static Map<String, EndPointHandler> ENDPOINT_TO_HANDLER = new HashMap<>();
    /**
     * Ĭ�ϵ�ʲô�������� attrHandler
     */
    public final static OperationAttrHandler DEFAULT_OPERATION_ATTR_HANDLER = new MapOperationAttrHandler(new DoNothingAttrHandler(), Types.String);

    static {
        ENDPOINT_TO_HANDLER.put(ATTRIBUTE, new AttributeHandler());
        ENDPOINT_TO_HANDLER.put(VALUES, new ValuesHandler());
        // ���������� ����д, ��Ϊ��EndPoint.ATTRIBUTE�ĳ�ʼ��������Constants.class�ļ���, ��ִ�е�ǰstaticBlock��ʱ��, Endpoint.ATTRIBUTE �Լ�EndPoint.VALUES�ڳ�ʼ���׶γ�ʼ����nullֵ		--2016.02.02
//		ENDPOINT_TO_HANDLER.put(EndPoint.ATTRIBUTE, new ValuesHandler() );
    }

}
