/**
 * file name : Attribute.java
 * created at : 7:57:53 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.interf.EndPoint;
import com.hx.json.JSONObject;

// attribute结点
public final class Attribute extends EndPoint {

    // attribute[text, innertext, innerhtml, outerhtml]
    private String attr;

    // 初始化
    public Attribute(String name, String xpath, String attr, String handlerStr, EndPoint parent) {
        super(EndPoint.ATTRIBUTE, name, xpath, handlerStr, parent);
        this.attr = attr;
    }

    // 添加子元素[attribute 不支持添加子节点]
    @Override
    public void addChild(EndPoint endPoint) {
        throw new RuntimeException("unsupported operation exception ...");
    }

    // 获取子元素[attribute 没有子元素]
    @Override
    public EndPoint getChild(int idx) {
        throw new RuntimeException("unsupported operation exception ...");
    }

    // Attribute结点的孩子个数为0
    @Override
    public int childSize() {
        return 0;
    }

    // 获取attr
    @Override
    public String getAttribute() {
        return attr;
    }

    // for debug ...
    public String toString() {
        return new JSONObject().element("fromEndPoint", super.toString()).element("attribute", attr).toString();
    }

}
