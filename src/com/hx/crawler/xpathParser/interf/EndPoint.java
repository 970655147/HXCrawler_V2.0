/**
 * file name : EndPoint.java
 * created at : 7:42:13 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.xpathParser.interf;

import com.hx.attrHandler.attrHandler.operation.interf.OperationAttrHandler;
import com.hx.attrHandler.util.HandlerParserUtil;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.log.util.Tools;

import net.sf.json.JSONObject;

// 一个EndPint[终端结点 : "values" & "attribute"]
public abstract class EndPoint {
	
	// 常量
	public static String VALUES = CrawlerConstants.VALUES;
	public static String ATTRIBUTE = CrawlerConstants.ATTRIBUTE;
	
	// 类型, 名称, xpath
	// 注意 : 对于values来说, xpath必需存在
		// attribute来说, xpath不必须
	protected String type;
	protected String name;
	protected String xpath;
	protected EndPoint parent;
	// mapHandler, filterHanlder, 以及是否被filter的标志位
	protected OperationAttrHandler handler;
	
	// 初始化
	public EndPoint(String type, String name, String xpath, String handlerStr, EndPoint parent) {
		this.type = type;
		this.name = name;
		this.parent = parent;
		this.xpath = xpath;
		
		initHandler(CrawlerConstants.HANDLER, handlerStr);
//		if(xpath != null) {
//			this.xpath = Tools.getXPath(this, xpath);
//		}
	}

	// 添加孩子, 获取, 孩子的个数
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
	
	// 初始化filter
	// 分为四种情况
	// 1. handlerString 为null, 直接继承parent的handler
	// 2. 如果handlerString以"+"开头, 并且parent的相应的handler为null  直接创建新的Handler
	// 3. 如果handlerString以"-"开头, 直接创建新的Handler
	// 4. 否则  combine parent的Handler, 和自己的Handler
	private void initHandler(String handlerType, String handlerStr) {
		if(handlerStr != null) {
			if((handlerStr.startsWith(CrawlerConstants.HANDLER_ADDED) 
					&& (CrawlerConstants.HANDLER.equals(handlerType) && (parent.handler == null)) )
					|| handlerStr.startsWith(CrawlerConstants.HANDLER_OVERRIDE)
					) {
					if(CrawlerConstants.HANDLER.equals(handlerType) ) {
						this.handler = HandlerParserUtil.handlerParse(handlerStr.substring(1), CrawlerConstants.HANDLER );
					} else {
						Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
					}
			} else if(handlerStr.startsWith(CrawlerConstants.HANDLER_ADDED)
					&& (CrawlerConstants.HANDLER.equals(handlerType) && (parent.handler != null) ) 
					) {
				if(CrawlerConstants.HANDLER.equals(handlerType) ) {
					this.handler = HandlerParserUtil.combineHandler(parent.handler, HandlerParserUtil.handlerParse(handlerStr.substring(1), CrawlerConstants.HANDLER) );
				} else {
					Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
				}
			} else {
				Tools.assert0("the handler should startWith : [" + CrawlerConstants.HANDLER_ADDED + ", " + CrawlerConstants.HANDLER_OVERRIDE + "], around : " + handlerStr );
			}
		} else {
			// default inhert from parent
			if(parent != null) {
				if(CrawlerConstants.HANDLER.equals(handlerType) ) {
					this.handler = parent.handler;
				} else {
					Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
				}
			} else {
				// doNothing
				this.handler = CrawlerConstants.defaultOperationAttrHandler;
			}
		}
	}
	
	// 通过传入的xpath, 获取真实的xpath
	// 如果xpath以 / 开头[/, //], 则直接返回xpath
	// 否则如果以.开头[./]  拼接上parent结点的xpath
	// 否则  表示xpath不存在, 或者不合理, 返回ep的父节点的xpath
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

//	// 确保xpath 不以"/" 结尾
//	protected static void makeSureNotEndsWithInvSlash(StringBuilder xPath) {
//		if(xPath.charAt(xPath.length()-1) == Constants.INV_SLASH) {
//			xPath.deleteCharAt(xPath.length() - 1);
//		}
//	}
	
}
