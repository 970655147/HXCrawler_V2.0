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
 * �����ַ��� : ���ڲ���ָ���Ľ��
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
     * ģ�����
     * �����ַ��� �п��ܵļ�Ϊname, xpath, attribute, values
     * name : ����JSONObject ����JSONArray�ļ�
     * xpath : ��Ҫ����xml�ĵ���
     * attribute : ���Խ��, JSONObject��һ������, ���������xpath�ܹ���ȡ��������, ��ôֻ��ȡ��һ�����,
     *  attribute ���ܵ�ֵΪ ":index", "text", "innertext", "innerhtml", "outerhtml", �Լ���ǰ������������������, attribute����xpath���п���
     *  :index : ��ʾ, ��ǰ�����values�����н���е�����, ��1��ʼ
     *  text : ��ʾ��ǰ����ڵ�����
     *  innertext : ��ʾ�ݹ鵱ǰ�������н�������
     *  innerhtml : ��ʾ�ݹ鵱ǰ�������н��ı�ǩ�Լ�����
     *  outerhtml : ��ʾ��innerhtml�Ļ������� ���һ����ǰ���ı�ǩ
     * values : ���ֵ�Ľ��, ��Ӧ��һ��JSONArray, values�еĶ��ģ��JSONObject��ÿһ����Ӧ������, ����װ��һ��JSONObject, name��Ϊkey, attribute ����values��Ϊֵ,
     *  values����xpath�������
     */

    /**
     * ��ʾ��ǰidxStr��Ӧ��JSONArray
     */
    private JSONArray idxArr;
    /**
     * ��ʾ��ǰ�������Ĳ㼶�ĵ�����ջ
     */
    private Deque<Iterator<?>> stack;
    /**
     * ��ʾ��ǰ��������"attribute" ����"values"
     */
    private Deque<Endpoint> valuesStack;
    /**
     * ��ʾ���ڵ�, ����������endpoint
     */
    private Endpoint root;

    /**
     * endpoint -> endpointHandler
     */
    private Map<String, EndpointHandler> endpoint2Handler;

    /**
     * ��ʼ��
     * ����root ���, ������
     *
     * @param idxStr           �����������ַ���
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
     * ��ȡ ��������� root ���
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

    // ----------------- �������� -----------------------

    /**
     * ����indexString, �����ݽӿں�root�����������
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
     * ��ȡ��һ��values ����attribute���
     * ÿ�� ��ȡһ��endpoint, �������values���, ����һ�λ�ȡ������values����е����� [����������ݹ��Ч��]
     * ... ֮ǰû����ϸ��ע��, ��λ�������  �����ҿ��˺þá���		--2015.10.02
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
     * �ж��Ƿ�����һ��Endpoint
     * ����������Ĳ㼶�ĵ�����ջ�����ڵ�����, ���ҵ������л���Ԫ��û�з���, �򷵻�true
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
     * У�鵱ǰ XpathIndexString ���������
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
