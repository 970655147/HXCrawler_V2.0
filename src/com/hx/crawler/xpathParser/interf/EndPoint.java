/**
 * file name : EndPoint.java
 * created at : 7:42:13 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.xpathParser.interf;

import net.sf.json.JSONObject;

import com.hx.attrHandler.attrHandler.operation.interf.OperationAttrHandler;
import com.hx.attrHandler.util.HandlerParserUtil;
import com.hx.crawler.util.Constants;
import com.hx.crawler.util.CrawlerUtils;
import com.hx.log.log.Tools;

// һ��EndPint[�ն˽�� : "values" & "attribute"]
public abstract class EndPoint {
	
	// ����
	public static String VALUES = Constants.VALUES;
	public static String ATTRIBUTE = Constants.ATTRIBUTE;
	
	// ����, ����, xpath
	// ע�� : ����values��˵, xpath�������
		// attribute��˵, xpath������
	protected String type;
	protected String name;
	protected String xpath;
	protected EndPoint parent;
	// mapHandler, filterHanlder, �Լ��Ƿ�filter�ı�־λ
	protected OperationAttrHandler handler;
	
	// ��ʼ��
	public EndPoint(String type, String name, String xpath, String handlerStr, EndPoint parent) {
		this.type = type;
		this.name = name;
		this.parent = parent;
		this.xpath = xpath;
		
		initHandler(Constants.HANDLER, handlerStr);
//		if(xpath != null) {
//			this.xpath = Tools.getXPath(this, xpath);
//		}
	}

	// ��Ӻ���, ��ȡ, ���ӵĸ���
	public abstract void addChild(EndPoint endPoint);
	public abstract EndPoint getChild(int idx);
	public abstract int childSize();
	public abstract String getAttribute();
	
	// for debug ...
	public String toString() {
		return new JSONObject().element("name", name).element("xpath", xpath).toString();
	}
	
	// getter
	public EndPoint getParent() {
		return parent;
	}
	public String getXPath() {
		return xpath;
	}
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public OperationAttrHandler getHandler() {
		return handler;
	}
	
	// ��ʼ��filter
	// ��Ϊ�������
	// 1. handlerString Ϊnull, ֱ�Ӽ̳�parent��handler
	// 2. ���handlerString��"+"��ͷ, ����parent����Ӧ��handlerΪnull  ֱ�Ӵ����µ�Handler
	// 3. ���handlerString��"-"��ͷ, ֱ�Ӵ����µ�Handler
	// 4. ����  combine parent��Handler, ���Լ���Handler
	private void initHandler(String handlerType, String handlerStr) {
		if(handlerStr != null) {
			if((handlerStr.startsWith(Constants.HANDLER_ADDED) 
					&& (Constants.HANDLER.equals(handlerType) && (parent.handler == null)) )
					|| handlerStr.startsWith(Constants.HANDLER_OVERRIDE)
					) {
					if(Constants.HANDLER.equals(handlerType) ) {
						this.handler = HandlerParserUtil.handlerParse(handlerStr.substring(1), Constants.HANDLER );
					} else {
						Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
					}
			} else if(handlerStr.startsWith(Constants.HANDLER_ADDED)
					&& (Constants.HANDLER.equals(handlerType) && (parent.handler != null) ) 
					) {
				if(Constants.HANDLER.equals(handlerType) ) {
					this.handler = HandlerParserUtil.combineHandler(parent.handler, HandlerParserUtil.handlerParse(handlerStr.substring(1), Constants.HANDLER) );
				} else {
					Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
				}
			} else {
				Tools.assert0("the handler should startWith : [" + Constants.HANDLER_ADDED + ", " + Constants.HANDLER_OVERRIDE + "], around : " + handlerStr );
			}
		} else {
			// default inhert from parent
			if(parent != null) {
				if(Constants.HANDLER.equals(handlerType) ) {
					this.handler = parent.handler;
				} else {
					Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
				}
			} else {
				// doNothing
				this.handler = Constants.defaultOperationAttrHandler;
			}
		}
	}
	
	// ͨ�������xpath, ��ȡ��ʵ��xpath
	// ���xpath�� / ��ͷ[/, //], ��ֱ�ӷ���xpath
	// ���������.��ͷ[./]  ƴ����parent����xpath
	// ����  ��ʾxpath������, ���߲�����, ����ep�ĸ��ڵ��xpath
//	protected static String getXPath(EndPoint ep, String xpath) {
//		xpath = xpath.trim();
//		if(xpath.startsWith("/") ) {
//			return xpath;
//		} else if(xpath.startsWith(".")) {
//			StringBuilder sb = new StringBuilder(ep.getParent().getXPath().length() + xpath.length() + 1);
//			sb.append(ep.getParent().getXPath() );
//			makeSureNotEndsWithInvSlash(sb);
//			sb.append(xpath, 1, xpath.length());
//			return sb.toString();
//		}
//		
//		return ep.getParent().getXPath();
//	}

//	// ȷ��xpath ����"/" ��β
//	protected static void makeSureNotEndsWithInvSlash(StringBuilder xPath) {
//		if(xPath.charAt(xPath.length()-1) == Constants.INV_SLASH) {
//			xPath.deleteCharAt(xPath.length() - 1);
//		}
//	}
	
}
