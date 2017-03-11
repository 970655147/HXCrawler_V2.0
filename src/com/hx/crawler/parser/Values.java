/**
 * file name : Values.java
 * created at : 8:03:17 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.hx.crawler.parser.interf.EndPoint;

// values 元素
public final class Values extends EndPoint {

    // values的子元素
    private List<EndPoint> childs;

    // 初始化
    public Values(String name, String xpath, String handlerStr, EndPoint parent) {
        super(EndPoint.VALUES, name, xpath, handlerStr, parent);
        childs = new ArrayList<>();
    }

    // 添加子元素
    @Override
    public void addChild(EndPoint endPoint) {
        childs.add(endPoint);
    }

    // 获取子元素
    @Override
    public EndPoint getChild(int idx) {
        return childs.get(idx);
    }

    // values 的孩子个数为childs的结点数目[不用递归]
    @Override
    public int childSize() {
        return childs.size();
    }

    // 获取attr [values 不支持此操作]
    @Override
    public String getAttribute() {
        throw new RuntimeException("unsupported operation exception ...");
    }

    // for debug ...
    public String toString() {
        return new JSONObject().element("fromEndPoint", super.toString()).element("childs", childs).toString();
    }

}
