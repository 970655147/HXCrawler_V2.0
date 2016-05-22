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

// 索引字符串 : 用于查找指定的结果
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

//	模板规则
//	索引字符串 中可能的键为name, xpath, attribute, values
//	name : 用作JSONObject 或者JSONArray的键
//	xpath : 主要用于xml的导航
//	attribute : 属性结点, JSONObject的一个数据, 如果给定的xpath能够获取到多个结点, 那么只获取第一个结点, attribute 可能的值为 ":index", "text", "innertext", "innerhtml", "outerhtml", 以及当前结点的其他的属性名称, attribute结点的xpath可有可无
//		:index : 表示, 当前结点在values中所有结点中的索引, 从1开始
//		text : 表示当前结点内的文字
//		innertext : 表示递归当前结点的所有结点的文字
//		innerhtml : 表示递归当前结点的所有结点的标签以及文字
//		outerhtml : 表示在innerhtml的基础上面 添加一个当前结点的标签
//	values : 多个值的结点, 对应于一个JSONArray, values中的多个模板JSONObject的每一个对应的数据, 并封装成一个JSONObject, name作为key, attribute 或者values作为值, values结点的xpath必需存在
	
	// idxArr 表示当前idxStr对应的JSONArray
	// stack 表示当前遍历到的层级的迭代器栈
	// valuesStack 表示当前遍历到的"attribute" 或者"values"
	// root 表示根节点, 其连接着子endpoint
	private JSONArray idxArr;
	private Deque<Iterator<?>> stack;
	private Deque<EndPoint> valuesStack;
	private EndPoint root;
	
	// 初始化
	// 创建root 结点, 并解析
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
	
	// 获取root结点
	public EndPoint getRoot() {
		return root;
	}
	
	// 解析indexString, 键数据接口和root结点连接起来
	private void parse() {
		while(hasNextEndPoint() ) {
			nextEndPoint();
		}
	}
	
	// 获取下一个values 或者attribute结点
	// 每次 获取一个endpoint, 如果存在values结点, 则下一次获取到的是values结点中的数据 [类似与先序递归的效果]
	// ... 之前没有详细的注释, 这次回来看看  还把我看了好久。。		--2015.10.02
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
	
	// 判断是否还有下一个EndPoint
	// 如果遍历的层级的迭代器栈还存在迭代器, 并且迭代器中还有元素没有访问, 则返回true
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
