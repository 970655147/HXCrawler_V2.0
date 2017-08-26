/**
 * file name : Attribute.java
 * created at : 7:57:53 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.crawler.parser.interf.Endpoint;
import com.hx.crawler.parser.interf.EndpointType;
import com.hx.json.JSONObject;

/**
 * attribute 结点
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:32 PM
 */
public final class Attribute extends Endpoint {

    /**
     * 当前 attribute 确定需要抓取的数据的属性
     * attribute[text, innertext, innerhtml, outerhtml, 其他属性节点]
     */
    private String attr;

    /**
     * 初始化
     *
     * @param name       name
     * @param xpath      xpath
     * @param attr       attr
     * @param handlerStr handlerStr
     * @param parent     parent
     * @since 1.0
     */
    public Attribute(String name, String xpath, String attr, String handlerStr, Endpoint parent) {
        super(EndpointType.ATTRIBUTE, name, xpath, handlerStr, parent);
        this.attr = attr;
    }

    @Override
    public void addChild(Endpoint endPoint) {
        throw new RuntimeException("unsupported operation exception ...");
    }

    @Override
    public Endpoint getChild(int idx) {
        throw new RuntimeException("unsupported operation exception ...");
    }

    @Override
    public int childSize() {
        return 0;
    }

    @Override
    public String getAttribute() {
        return attr;
    }

    /**
     * for debug ...
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/11/2017 8:34 PM
     * @since 1.0
     */
    public String toString() {
        return new JSONObject().element("super", super.toString()).element("attribute", attr).toString();
    }

}
