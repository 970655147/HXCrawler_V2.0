/**
 * file name : ValuesHandler.java
 * created at : 2:14:16 PM Feb 2, 2016
 * created by 970655147
 */

package com.hx.crawler.parser;

import com.hx.common.interf.common.Result;
import com.hx.common.util.ResultUtils;
import com.hx.crawler.parser.interf.Endpoint;
import com.hx.crawler.parser.interf.EndpointHandler;
import com.hx.crawler.parser.interf.Parser;
import com.hx.crawler.parser.xpathImpl.XPathParser;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;
import com.hx.log.util.Tools;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * values 结点的相关业务处理
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/11/2017 8:38 PM
 */
public final class ValuesHandler extends EndpointHandler {

    /**
     * parser
     */
    private Parser parser;

    public ValuesHandler(Parser parser) {
        this.parser = parser;
    }

    public ValuesHandler() {
        this(new XPathParser());
    }

    @Override
    public Result validate(Endpoint endpoint, JSONObject current) {
        if (Tools.isEmpty(endpoint.getName())) {
            return ResultUtils.failed("the attribute 'name' can't be null !");
        }
        JSONArray valuesAttr = current.optJSONArray(CrawlerConstants.VALUES, null);
        if (Tools.isEmpty(valuesAttr)) {
            return ResultUtils.failed("the attribute 'values' can't be null !");
        }
        if (!checkCompatible(valuesAttr)) {
            throw new RuntimeException("the valuesNode : " + endpoint.getName() + ", xpath : " + endpoint.getXPath() +
                    " is not compatible with current version of HXCrawler_V2.0 !");
        }

        return ResultUtils.success();
    }

    @Override
    public Endpoint createInstance(Endpoint parent, JSONObject current) {
        String name = current.optString(CrawlerConstants.NAME, null);
        String xpath = current.optString(CrawlerConstants.XPATH, null);
        String handlerStr = current.optString(CrawlerConstants.HANDLER, null);

        Values result = new Values(name, xpath, handlerStr, parent);
        return result;
    }

    @Override
    public void handle(Element root, Element currentEle, String url, JSONArray res, int idx,
                       Endpoint child, JSONObject curObj) {
        List<Element> eles = new ArrayList<>();
        if (Tools.isEmpty(child.getXPath())) {
            eles.add(currentEle);
        } else {
            eles.addAll(XPathParser.getResultByXPath(root, currentEle, child.getXPath()));
        }

        JSONArray curArr = new JSONArray();
        int idx2 = 0;
        for (Element ele : eles) {
            parser.parse(root, ele, url, child, curArr, idx2++);
        }

        curObj.element(child.getName(), curArr);
    }

    /**
     * 校验给定的Values的兼容性 [不能同时存在name为"ArrayAttribute" 和name为其他字符串的属性]
     *
     * @param values 需要校验的 Values 节点
     * @return boolean
     * @author Jerry.X.He
     * @date 5/11/2017 8:38 PM
     * @since 1.0
     */
    private static boolean checkCompatible(JSONArray values) {
        boolean isContainArrayAttribute = false, isContainOther = false;
        for (int i = 0, len = values.size(); i < len; i++) {
            JSONObject childValue = values.optJSONObject(i, null);
            Tools.assert0(childValue != null, "the " + i + "th child of [" + values.toString() + "] is not good format !");

            if (CrawlerConstants.ARRAY_ATTR.equals(childValue.optString(CrawlerConstants.NAME))) {
                isContainArrayAttribute = true;
            } else {
                isContainOther = true;
            }
        }

        return !(isContainArrayAttribute && isContainOther);
    }
}
