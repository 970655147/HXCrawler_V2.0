/**
 * file name : IndexString.java
 * created at : 7:30:20 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.crawler.xpathParser;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.hx.crawler.util.Constants;
import com.hx.crawler.xpathParser.interf.EndPoint;
import com.hx.crawler.xpathParser.interf.IndexString;

// �����ַ��� : ���ڲ���ָ���Ľ��
//[
//{
//    "name":"product",
//    "xpath":"/html/body/product",
//    "values":[
//        {
//            "name":"fullName",
//            "attribute":"text"
//        }
//    ]
//}
//]
public final class XpathIndexString extends IndexString {

//	ģ�����
//	�����ַ��� �п��ܵļ�Ϊname, xpath, attribute, values
//	name : ����JSONObject ����JSONArray�ļ�
//	xpath : ��Ҫ����xml�ĵ���
//	attribute : ���Խ��, JSONObject��һ������, ���������xpath�ܹ���ȡ��������, ��ôֻ��ȡ��һ�����, attribute ���ܵ�ֵΪ ":index", "text", "innertext", "innerhtml", "outerhtml", �Լ���ǰ������������������, attribute����xpath���п���
//		:index : ��ʾ, ��ǰ�����values�����н���е�����, ��1��ʼ
//		text : ��ʾ��ǰ����ڵ�����
//		innertext : ��ʾ�ݹ鵱ǰ�������н�������
//		innerhtml : ��ʾ�ݹ鵱ǰ�������н��ı�ǩ�Լ�����
//		outerhtml : ��ʾ��innerhtml�Ļ������� ���һ����ǰ���ı�ǩ
//	values : ���ֵ�Ľ��, ��Ӧ��һ��JSONArray, values�еĶ��ģ��JSONObject��ÿһ����Ӧ������, ����װ��һ��JSONObject, name��Ϊkey, attribute ����values��Ϊֵ, values����xpath�������
	
	// idxArr ��ʾ��ǰidxStr��Ӧ��JSONArray
	// stack ��ʾ��ǰ�������Ĳ㼶�ĵ�����ջ
	// valuesStack ��ʾ��ǰ��������"attribute" ����"values"
	// root ��ʾ���ڵ�, ����������endpoint
	private JSONArray idxArr;
	private Deque<Iterator<?>> stack;
	private Deque<EndPoint> valuesStack;
	private EndPoint root;
	
	// ��ʼ��
	// ����root ���, ������
	public XpathIndexString(String idxStr) {
		super(idxStr);
		idxArr = JSONArray.fromObject(idxStr );
		stack = new LinkedList<>();
		valuesStack = new LinkedList<>();
		stack.push(idxArr.iterator());
		root = new Values(Constants.ROOT, null, null, null);
		valuesStack.push(root);
		
		parse();
	}
	
	// ��ȡroot���
	public EndPoint getRoot() {
		return root;
	}
	
	// ����indexString, �����ݽӿں�root�����������
	private void parse() {
		while(hasNextEndPoint() ) {
			nextEndPoint();
		}
	}
	
	// ��ȡ��һ��values ����attribute���
	// ÿ�� ��ȡһ��endpoint, �������values���, ����һ�λ�ȡ������values����е����� [����������ݹ��Ч��]
	// ... ֮ǰû����ϸ��ע��, ��λ�������  �����ҿ��˺þá���		--2015.10.02
	private EndPoint nextEndPoint() {
		EndPoint res = null;
		Iterator<?> it = null;
		while(!stack.isEmpty() ) {
			it = stack.peek();
			if(it.hasNext() ) {
				JSONObject current = (JSONObject) it.next();
				if(current.containsKey(EndPoint.VALUES) ) {
					JSONArray values = current.getJSONArray(EndPoint.VALUES);
					res = new Values(current.getString(Constants.NAME), current.getString(Constants.XPATH),
									current.optString(Constants.HANDLER, null), valuesStack.peek() 
									);
					stack.push(values.iterator() );
					valuesStack.peek().addChild(res);
					valuesStack.push(res);
					break ;
				} else if(current.containsKey(EndPoint.ATTRIBUTE) ) {
					res = new Attribute(current.optString(Constants.NAME, Constants.ARRAY_ATTR), 
										current.optString(Constants.XPATH, null), current.getString(EndPoint.ATTRIBUTE), 
										current.optString(Constants.HANDLER, null), valuesStack.peek() 
										);
					valuesStack.peek().addChild(res);
					break ;
				}
			} else {
				stack.pop();
				valuesStack.pop();
			}
		}
		
		return res;
	}
	
	// �ж��Ƿ�����һ��EndPoint
	// ��������Ĳ㼶�ĵ�����ջ�����ڵ�����, ���ҵ������л���Ԫ��û�з���, �򷵻�true
	private boolean hasNextEndPoint() {
		while(!stack.isEmpty() ) {
			if(stack.peek().hasNext() ) {
				return true;
			} else {
				stack.pop();
				valuesStack.pop();
			}
		}
		
		return false;
	}
	
	// for debug ...
	public String toString() {
//		Iterator<EndPoint> it = valuesStack.iterator();
//		while(it.hasNext()) {
//			Log.log(it.next().toString() );
//		}
		return "[ " + root.toString() + " ]";
	}
	
}
