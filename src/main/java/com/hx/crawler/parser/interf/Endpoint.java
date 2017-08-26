/**
 * file name : EndPoint.java
 * created at : 7:42:13 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import com.hx.attr_handler.attr_handler.operation.interf.OperationAttrHandler;
import com.hx.attr_handler.util.AttrHandlerUtils;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONObject;
import com.hx.log.util.Tools;

/**
 * 一个EndPint[终端结点 : "values" & "attribute"]
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:13 PM
 */
public abstract class Endpoint {

    /**
     * 当前节点的类型, attribute 还是 values [目前是这两个, 用于映射 attrHandler]
     */
    protected EndpointType type;
    /**
     * 当前节点的 name [在结果中作为key]
     */
    protected String name;
    /**
     * 当前节点的 xpath [用于索引数据][对于values来说, 必须存在, 对于attribute不必必须存在]
     */
    protected String xpath;
    /**
     * 当前节点的父节点
     */
    protected Endpoint parent;
    /**
     * 当前节点上面附加的 attrHandler
     */
    protected OperationAttrHandler handler;

    /**
     * 初始化
     *
     * @param type       type
     * @param name       name
     * @param xpath      xpath
     * @param handlerStr handlerString
     * @param parent     parent
     * @since 1.0
     */
    public Endpoint(EndpointType type, String name, String xpath, String handlerStr, Endpoint parent) {
        this.type = type;
        this.name = name;
        this.parent = parent;
        this.xpath = xpath;

        initHandler(CrawlerConstants.HANDLER, handlerStr);
    }

    /**
     * 添加孩子节点
     * attribute : 没有孩子节点 不支持
     * values : 添加子节点
     *
     * @param endPoint 给定的节点
     * @author Jerry.X.He
     * @date 5/11/2017 8:18 PM
     * @since 1.0
     */
    public abstract void addChild(Endpoint endPoint);

    /**
     * 获取索引处的孩子节点
     * attribute : 没有孩子节点 不支持
     * values : 获取子节点
     *
     * @param idx 给定的索引
     * @author Jerry.X.He
     * @date 5/11/2017 8:18 PM
     * @since 1.0
     */
    public abstract Endpoint getChild(int idx);

    /**
     * 获取孩子的数量
     *
     * @author Jerry.X.He
     * @date 5/11/2017 8:18 PM
     * @since 1.0
     */
    public abstract int childSize();

    /**
     * 获取属性配置
     * attribute : 获取提取目标节点的属性
     * values : 无属性
     *
     * @author Jerry.X.He
     * @date 5/11/2017 8:18 PM
     * @since 1.0
     */
    public abstract String getAttribute();

    /**
     * for debug ...
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/11/2017 8:20 PM
     * @since 1.0
     */
    public String toString() {
        return new JSONObject().element("name", name).element("xpath", xpath).toString();
    }

    /**
     * getter
     */
    public Endpoint getParent() {
        return parent;
    }

    public String getXPath() {
        return xpath;
    }

    public EndpointType getType() {
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
     * @author 970655147 created at 2017-03-11 11:39
     */
    private void initHandler(String handlerType, String handlerStr) {
        if (handlerStr != null) {
            if ((handlerStr.startsWith(CrawlerConstants.HANDLER_ADDED)
                    && (CrawlerConstants.HANDLER.equals(handlerType) && (parent.handler == null)))
                    || (handlerStr.startsWith(CrawlerConstants.HANDLER_OVERRIDE)
                    && CrawlerConstants.HANDLER.equals(handlerType))
                    ) {
                if (CrawlerConstants.HANDLER.equals(handlerType)) {
                    this.handler = AttrHandlerUtils.handlerParse(handlerStr.substring(1), CrawlerConstants.HANDLER);
                } else {
                    Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
                }
            } else if (handlerStr.startsWith(CrawlerConstants.HANDLER_ADDED)
                    && (CrawlerConstants.HANDLER.equals(handlerType) && (parent.handler != null))
                    ) {
                if (CrawlerConstants.HANDLER.equals(handlerType)) {
                    this.handler = AttrHandlerUtils.combineHandler(parent.handler, AttrHandlerUtils.handlerParse(handlerStr.substring(1), CrawlerConstants.HANDLER));
                } else {
                    Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
                }
            } else {
                Tools.assert0("the handler should startWith : [" + CrawlerConstants.HANDLER_ADDED + ", " + CrawlerConstants.HANDLER_OVERRIDE + "], around : " + handlerStr);
            }
        } else {
            // default inhert from parent
            if (parent != null) {
                if (CrawlerConstants.HANDLER.equals(handlerType)) {
                    this.handler = parent.handler;
                } else {
                    Tools.assert0("have no this handlerType : " + handlerType + ", please check it !");
                }
            } else {
                // doNothing
                this.handler = CrawlerConstants.DEFAULT_OPERATION_ATTR_HANDLER;
            }
        }
    }

}
