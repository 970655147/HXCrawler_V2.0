/**
 * file name : IndexString.java
 * created at : 13:32:20 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.parser.interf;

// 索引字符串接口
public abstract class IndexString {

	// 创建root 结点, 并解析
	public IndexString(String idxStr) {
		
	}

	/**
	 * 获取索引字符串解析之后的根节点
	 *
	 * @return com.hx.crawler.parser.interf.EndPoint
	 * @throws
	 * @author 970655147 created at 2017-03-11 12:02
	 */
	public abstract EndPoint getRoot();
	
}
