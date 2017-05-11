/**
 * file name : IndexString.java
 * created at : 7:30:20 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.parser.xpathImpl;

import com.hx.crawler.parser.Attribute;
import com.hx.crawler.parser.Values;
import com.hx.crawler.parser.interf.EndPoint;
import com.hx.crawler.parser.interf.IndexString;
import com.hx.crawler.util.CrawlerConstants;
import com.hx.json.JSONArray;
import com.hx.json.JSONObject;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

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
    private Deque<EndPoint> valuesStack;
    /**
     * ��ʾ���ڵ�, ����������endpoint
     */
    private EndPoint root;

    /**
     * ��ʼ��
     * ����root ���, ������
     *
     * @param idxStr �����������ַ���
     * @since 1.0
     */
    public XpathIndexString(String idxStr) {
        super(idxStr);
        idxArr = JSONArray.fromObject(idxStr);
        stack = new LinkedList<>();
        valuesStack = new LinkedList<>();
        stack.push(idxArr.iterator());
        root = new Values(CrawlerConstants.ROOT, null, null, null);
        valuesStack.push(root);

        parse();
    }

    /**
     * ��ȡ ��������� root ���
     *
     * @return com.hx.crawler.parser.interf.EndPoint
     * @author Jerry.X.He
     * @date 5/11/2017 8:40 PM
     * @since 1.0
     */
    public EndPoint getRoot() {
        return root;
    }

    /**
     * ����indexString, �����ݽӿں�root�����������
     *
     * @return void
     * @author Jerry.X.He
     * @date 5/11/2017 8:40 PM
     * @since 1.0
     */
    private void parse() {
        while (hasNextEndPoint()) {
            nextEndPoint();
        }
    }

    /**
     * ��ȡ��һ��values ����attribute���
     * ÿ�� ��ȡһ��endpoint, �������values���, ����һ�λ�ȡ������values����е����� [����������ݹ��Ч��]
     * ... ֮ǰû����ϸ��ע��, ��λ�������  �����ҿ��˺þá���		--2015.10.02
     *
     * @return com.hx.crawler.parser.interf.EndPoint
     * @author Jerry.X.He
     * @date 5/11/2017 8:39 PM
     * @since 1.0
     */
    private EndPoint nextEndPoint() {
        EndPoint res = null;
        Iterator<?> it = null;
        while (!stack.isEmpty()) {
            it = stack.peek();
            if (it.hasNext()) {
                JSONObject current = (JSONObject) it.next();
                if (current.containsKey(EndPoint.VALUES)) {
                    JSONArray values = current.getJSONArray(EndPoint.VALUES);
                    res = new Values(current.getString(CrawlerConstants.NAME), current.getString(CrawlerConstants.XPATH),
                            current.optString(CrawlerConstants.HANDLER, null), valuesStack.peek()
                    );
                    stack.push(values.iterator());
                    valuesStack.peek().addChild(res);
                    valuesStack.push(res);
                    break;
                } else if (current.containsKey(EndPoint.ATTRIBUTE)) {
                    res = new Attribute(current.optString(CrawlerConstants.NAME, CrawlerConstants.ARRAY_ATTR),
                            current.optString(CrawlerConstants.XPATH, null), current.getString(EndPoint.ATTRIBUTE),
                            current.optString(CrawlerConstants.HANDLER, null), valuesStack.peek()
                    );
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
     * �ж��Ƿ�����һ��EndPoint
     * ����������Ĳ㼶�ĵ�����ջ�����ڵ�����, ���ҵ������л���Ԫ��û�з���, �򷵻�true
     *
     * @return boolean
     * @author Jerry.X.He
     * @date 5/11/2017 8:44 PM
     * @since 1.0
     */
    private boolean hasNextEndPoint() {
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

}
