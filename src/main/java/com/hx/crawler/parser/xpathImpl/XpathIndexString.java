/**
 * file name : IndexString.java
 * created at : 7:30:20 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.xpathImpl;

import com.hx.common.interf.common.Result;
import com.hx.crawler.parser.Values;
import com.hx.crawler.parser.interf.Endpoint;
import com.hx.crawler.parser.interf.EndpointHandler;
import com.hx.crawler.parser.interf.EndpointType;
import com.hx.crawler.parser.interf.IndexString;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;
import com.hx.log.util.Tools;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * 索引字符串 : 用于查找指定的结果
 * [
 * {
 * "name":"product",
 * "xpath":"/html/body/product",
 * "values":[
 * {
 * "name":"fullName",
 * "attribute":"text"
 * }
 * ]
 * }
 * ]
 */
public final class XpathIndexString extends IndexString {

    /**
     * 模板规则
     * 索引字符串 中可能的键为name, xpath, attribute, values
     * name : 用作JSONObject 或者JSONArray的键
     * xpath : 主要用于xml的导航
     * attribute : 属性结点, JSONObject的一个数据, 如果给定的xpath能够获取到多个结点, 那么只获取第一个结点,
     *  attribute 可能的值为 ":index", "text", "innertext", "innerhtml", "outerhtml", 以及当前结点的其他的属性名称, attribute结点的xpath可有可无
     *  :index : 表示, 当前结点在values中所有结点中的索引, 从1开始
     *  text : 表示当前结点内的文字
     *  innertext : 表示递归当前结点的所有结点的文字
     *  innerhtml : 表示递归当前结点的所有结点的标签以及文字
     *  outerhtml : 表示在innerhtml的基础上面 添加一个当前结点的标签
     * values : 多个值的结点, 对应于一个JSONArray, values中的多个模板JSONObject的每一个对应的数据, 并封装成一个JSONObject, name作为key, attribute 或者values作为值,
     *  values结点的xpath必需存在
     */

    /**
     * 表示当前idxStr对应的JSONArray
     */
    private JSONArray idxArr;
    /**
     * 表示当前遍历到的层级的迭代器栈
     */
    private Deque<Iterator<?>> stack;
    /**
     * 表示当前遍历到的"attribute" 或者"values"
     */
    private Deque<Endpoint> valuesStack;
    /**
     * 表示根节点, 其连接着子endpoint
     */
    private Endpoint root;

    /**
     * endpoint -> endpointHandler
     */
    private Map<String, EndpointHandler> endpoint2Handler;

    /**
     * 初始化
     * 创建root 结点, 并解析
     *
     * @param idxStr           给定的索引字符串
     * @param endpoint2Handler endpoint -> endpointHandler
     * @since 1.0
     */
    public XpathIndexString(String idxStr, Map<String, EndpointHandler> endpoint2Handler) {
        super(idxStr);
        idxArr = JSONArray.fromObject(idxStr);
        stack = new LinkedList<>();
        valuesStack = new LinkedList<>();
        root = new Values(CrawlerConstants.ROOT, null, null, null);
        this.endpoint2Handler = endpoint2Handler;

        stack.push(idxArr.iterator());
        valuesStack.push(root);
        preCheck();
        parse();
    }

    public XpathIndexString(String idxStr) {
        this(idxStr, CrawlerConstants.ENDPOINT_TO_HANDLER);
    }

    /**
     * 获取 解析结果的 root 结点
     *
     * @return com.hx.crawler.parser.interf.Endpoint
     * @author Jerry.X.He
     * @date 5/11/2017 8:40 PM
     * @since 1.0
     */
    public Endpoint getRoot() {
        return root;
    }

    /**
     * for debug ...
     *
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/11/2017 8:44 PM
     * @since 1.0
     */
    public String toString() {
        return "[ " + root.toString() + " ]";
    }

    // ----------------- 辅助方法 -----------------------

    /**
     * 解析indexString, 将数据接口和root结点连接起来
     *
     * @return void
     * @author Jerry.X.He
     * @date 5/11/2017 8:40 PM
     * @since 1.0
     */
    private void parse() {
        while (hasNextEndpoint()) {
            nextEndpoint();
        }
    }

    /**
     * 获取下一个values 或者attribute结点
     * 每次 获取一个endpoint, 如果存在values结点, 则下一次获取到的是values结点中的数据 [类似与先序递归的效果]
     * ... 之前没有详细的注释, 这次回来看看  还把我看了好久。。		--2015.10.02
     *
     * @return com.hx.crawler.parser.interf.Endpoint
     * @author Jerry.X.He
     * @date 5/11/2017 8:39 PM
     * @since 1.0
     */
    private Endpoint nextEndpoint() {
        Endpoint res = null;
        Iterator<?> it = null;
        while (!stack.isEmpty()) {
            it = stack.peek();
            if (it.hasNext()) {
                JSONObject current = (JSONObject) it.next();
                if (current.containsKey(EndpointType.VALUES.type())) {
                    String type = EndpointType.VALUES.type();
                    EndpointHandler handler = endpoint2Handler.get(type);
                    res = handler.createInstance(valuesStack.peek(), current);
                    Result validateResult = handler.validate(res, current);
                    if (!validateResult.isSuccess()) {
                        Tools.assert0(validateResult.getMsg());
                    }

                    JSONArray values = current.getJSONArray(type);
                    stack.push(values.iterator());
                    valuesStack.peek().addChild(res);
                    valuesStack.push(res);
                    break;
                } else if (current.containsKey(EndpointType.ATTRIBUTE.type())) {
                    String type = EndpointType.ATTRIBUTE.type();
                    EndpointHandler handler = endpoint2Handler.get(type);
                    res = handler.createInstance(valuesStack.peek(), current);
                    Result validateResult = handler.validate(res, current);
                    if (!validateResult.isSuccess()) {
                        Tools.assert0(JSONObject.fromObject(validateResult).toString());
                    }

                    valuesStack.peek().addChild(res);
                    break;
                }
            } else {
                stack.pop();
                valuesStack.pop();
            }
        }

        return res;
    }

    /**
     * 判断是否还有下一个Endpoint
     * 如果果遍历的层级的迭代器栈还存在迭代器, 并且迭代器中还有元素没有访问, 则返回true
     *
     * @return boolean
     * @author Jerry.X.He
     * @date 5/11/2017 8:44 PM
     * @since 1.0
     */
    private boolean hasNextEndpoint() {
        while (!stack.isEmpty()) {
            if (stack.peek().hasNext()) {
                return true;
            } else {
                stack.pop();
                valuesStack.pop();
            }
        }

        return false;
    }

    /**
     * 校验当前 XpathIndexString 的相关配置
     *
     * @return void
     * @author Jerry.X.He
     * @date 8/26/2017 7:27 PM
     * @since 1.0
     */
    private void preCheck() {
        for (Map.Entry<String, EndpointHandler> entry : CrawlerConstants.ENDPOINT_TO_HANDLER.entrySet()) {
            String key = entry.getKey();
            EndpointHandler handler = endpoint2Handler.get(key);
            Tools.assert0(handler != null, "the handler of '" + key + "' is null, please register it ! ");
        }
    }

}
