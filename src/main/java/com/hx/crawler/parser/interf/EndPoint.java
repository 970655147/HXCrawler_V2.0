/**
 * file name : EndPoint.java
 * created at : 7:42:13 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import com.hx.attr_handler.attr_handler.operation.interf.OperationAttrHandler;
import com.hx.attr_handler.util.AttrHandlerUtils;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.log.util.Tools;

import com.hx.json.JSONObject;

// һ��EndPint[�ն˽�� : "values" & "attribute"]
public abstract class EndPoint {

    // ����
    public static final String VALUES = CrawlerConstants.VALUES;
    public static final String ATTRIBUTE = CrawlerConstants.ATTRIBUTE;

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

        initHandler(CrawlerConstants.HANDLER, handlerStr);
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

    /**
     * ��ʼ��handler, ��Ϊ���¼������
     * 1. handlerString Ϊnull, ֱ�Ӽ̳�parent��handler
     * 2. handlerString ���ڵ������
     * 2.1. ���handlerString��"+"��ͷ, ����parent����Ӧ��handlerΪnull  ֱ�Ӵ����µ�Handler, ����combine parent��handler ���Լ���handler
     * 2.2. ���handlerString��"-"��ͷ, ֱ�Ӵ����µ�Handler
     * 2.3. ����  combine parent��Handler, ���Լ���Handler
     *
     * @param handlerType handler������
     * @param handlerStr  handler���ַ�����ʾ
     * @return void
     * @throws
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
