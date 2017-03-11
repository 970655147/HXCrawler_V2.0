/**
 * file name : EndPointHandler.java
 * created at : 2:02:38 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Element;

// 当碰到具体的Endpoint的时候, 处理其逻辑
public abstract class EndPointHandler {

    /**
     * 根据给定的资源, 解析当前结点的数据, 放入最终的结果集
     *
     * @param root       解析的doc的根节点
     * @param currentEle doc中当前结点
     * @param url        解析文档的url
     * @param res        解析最终的结果集
     * @param idx        如果是解析ValueNode的子元素的话, 传入当前索引
     * @param child      当前结点
     * @param curObj     当前结点对应的结果集
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 11:54
     */
    public abstract void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
                                EndPoint child, JSONObject curObj);

}
