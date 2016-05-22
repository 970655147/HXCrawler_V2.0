/**
 * file name : IndexString.java
 * created at : 13:32:20 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.xpathParser.interf;

// 索引字符串接口
public abstract class IndexString {

	// 创建root 结点, 并解析
	public IndexString(String idxStr) {
		
	}

	// 获取解析之后的root结点
	public abstract EndPoint getRoot();
	
}
