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
 * һ��EndPint[�ն˽�� : "values" & "attribute"]
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:13 PM
 */
public abstract class Endpoint {

    /**
     * ��ǰ�ڵ������, attribute ���� values [Ŀǰ��������, ����ӳ�� attrHandler]
     */
    protected EndpointType type;
    /**
     * ��ǰ�ڵ�� name [�ڽ������Ϊkey]
     */
    protected String name;
    /**
     * ��ǰ�ڵ�� xpath [������������][����values��˵, �������, ����attribute���ر������]
     */
    protected String xpath;
    /**
     * ��ǰ�ڵ�ĸ��ڵ�
     */
    protected Endpoint parent;
    /**
     * ��ǰ�ڵ����渽�ӵ� attrHandler
     */
    protected OperationAttrHandler handler;

    /**
     * ��ʼ��
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
     * ��Ӻ��ӽڵ�
     * attribute : û�к��ӽڵ� ��֧��
     * values : ����ӽڵ�
     *
     * @param endPoint �����Ľڵ�
     * @author Jerry.X.He
     * @date 5/11/2017 8:18 PM
     * @since 1.0
     */
    public abstract void addChild(Endpoint endPoint);

    /**
     * ��ȡ�������ĺ��ӽڵ�
     * attribute : û�к��ӽڵ� ��֧��
     * values : ��ȡ�ӽڵ�
     *
     * @param idx ����������
     * @author Jerry.X.He
     * @date 5/11/2017 8:18 PM
     * @since 1.0
     */
    public abstract Endpoint getChild(int idx);

    /**
     * ��ȡ���ӵ�����
     *
     * @author Jerry.X.He
     * @date 5/11/2017 8:18 PM
     * @since 1.0
     */
    public abstract int childSize();

    /**
     * ��ȡ��������
     * attribute : ��ȡ��ȡĿ��ڵ������
     * values : ������
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
