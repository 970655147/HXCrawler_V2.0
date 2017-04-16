/**
 * file name : EndPoint.java
 * created at : 7:42:13 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import com.hx.attr_handler.attr_handler.operation.interf.OperationAttrHandler;
import com.hx.attr_handler.util.AttrHandlerUtils;
import com.hx.crawler.util.HXCrawlerConstants;
import com.hx.log.util.Tools;

import com.hx.json.JSONObject;

// 一个EndPint[终端结点 : "values" & "attribute"]
public abstract class EndPoint {

    // 常量
    public static final String VALUES = HXCrawlerConstants.VALUES;
    public static final String ATTRIBUTE = HXCrawlerConstants.ATTRIBUTE;

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

        initHandler(HXCrawlerConstants.HANDLER, handlerStr);
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

    /**
     * 初始化handler, 分为如下几种情况
     * 1. handlerString 为null, 直接继承parent的handler
     * 2. handlerString 存在的情况下
     * 2.1. 如果handlerString以"+"开头, 并且parent的相应的handler为null  直接创建新的Handler, 否则combine parent的handler 和自己的handler
     * 2.2. 如果handlerString以"-"开头, 直接创建新的Handler
     * 2.3. 否则  combine parent的Handler, 和自己的Handler
     *
     * @param handlerType handler的类型
     * @param handlerStr  handler的字符串表示
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 11:39
     */
    private void initHandler(String handlerType, String handlerStr) {
        if (handlerStr != null) {
            if ((handlerStr.startsWith(HXCrawlerConstants.HANDLER_ADDED)
                    && (HXCrawlerConstants.HANDLER.equals(handlerType) && (parent.handler == null)))
                    || (handlerStr.startsWith(HXCrawlerConstants.HANDLER_OVERRIDE)
                    && HXCrawlerConstants.HANDLER.equals(handlerType))
                    ) {
                if (HXCrawlerConstants.HANDLER.equals(handlerType)) {
                    this.handler = AttrHandlerUtils.handlerParse(handlerStr.substring(1), HXCrawlerConstants.HANDLER);
                } else {
                    Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
                }
            } else if (handlerStr.startsWith(HXCrawlerConstants.HANDLER_ADDED)
                    && (HXCrawlerConstants.HANDLER.equals(handlerType) && (parent.handler != null))
                    ) {
                if (HXCrawlerConstants.HANDLER.equals(handlerType)) {
                    this.handler = AttrHandlerUtils.combineHandler(parent.handler, AttrHandlerUtils.handlerParse(handlerStr.substring(1), HXCrawlerConstants.HANDLER));
                } else {
                    Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
                }
            } else {
                Tools.assert0("the handler should startWith : [" + HXCrawlerConstants.HANDLER_ADDED + ", " + HXCrawlerConstants.HANDLER_OVERRIDE + "], around : " + handlerStr);
            }
        } else {
            // default inhert from parent
            if (parent != null) {
                if (HXCrawlerConstants.HANDLER.equals(handlerType)) {
                    this.handler = parent.handler;
                } else {
                    Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
                }
            } else {
                // doNothing
                this.handler = HXCrawlerConstants.DEFAULT_OPERATION_ATTR_HANDLER;
            }
        }
    }

}
