/**
 * file name : Values.java
 * created at : 8:03:17 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.interf.EndPoint;
import com.hx.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * values 元素
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:37 PM
 */
public final class Values extends EndPoint {

    /**
     * values的子元素
     */
    private List<EndPoint> childs;

    /**
     * 初始化
     *
     * @param name       name
     * @param xpath      xpath
     * @param handlerStr handlerStr
     * @param parent     parent
     * @since 1.0
     */
    public Values(String name, String xpath, String handlerStr, EndPoint parent) {
        super(EndPoint.VALUES, name, xpath, handlerStr, parent);
        childs = new ArrayList<>();
    }

    @Override
    public void addChild(EndPoint endPoint) {
        childs.add(endPoint);
    }

    @Override
    public EndPoint getChild(int idx) {
        return childs.get(idx);
    }

    @Override
    public int childSize() {
        return childs.size();
    }

    @Override
    public String getAttribute() {
        throw new RuntimeException("unsupported operation exception ...");
    }

    /**
     * for debug ...
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/11/2017 8:38 PM
     * @since 1.0
     */
    public String toString() {
        return new JSONObject().element("super", super.toString()).element("childs", childs).toString();
    }

}
