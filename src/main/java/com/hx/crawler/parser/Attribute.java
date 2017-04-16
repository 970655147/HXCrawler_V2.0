/**
 * file name : Attribute.java
 * created at : 7:57:53 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.interf.EndPoint;
import com.hx.json.JSONObject;

// attribute���
public final class Attribute extends EndPoint {

    // attribute[text, innertext, innerhtml, outerhtml]
    private String attr;

    // ��ʼ��
    public Attribute(String name, String xpath, String attr, String handlerStr, EndPoint parent) {
        super(EndPoint.ATTRIBUTE, name, xpath, handlerStr, parent);
        this.attr = attr;
    }

    // �����Ԫ��[attribute ��֧������ӽڵ�]
    @Override
    public void addChild(EndPoint endPoint) {
        throw new RuntimeException("unsupported operation exception ...");
    }

    // ��ȡ��Ԫ��[attribute û����Ԫ��]
    @Override
    public EndPoint getChild(int idx) {
        throw new RuntimeException("unsupported operation exception ...");
    }

    // Attribute���ĺ��Ӹ���Ϊ0
    @Override
    public int childSize() {
        return 0;
    }

    // ��ȡattr
    @Override
    public String getAttribute() {
        return attr;
    }

    // for debug ...
    public String toString() {
        return new JSONObject().element("fromEndPoint", super.toString()).element("attribute", attr).toString();
    }

}
