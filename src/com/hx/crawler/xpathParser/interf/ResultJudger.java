/**
 * file name : ResultJudger.java
 * created at : 5:11:13 PM Oct 25, 2015
 * created by 970655147
 */

package com.hx.crawler.xpathParser.interf;

import net.sf.json.JSONArray;

// 结果判断接口
public interface ResultJudger {

	// 判断给定的结果是否为空
	public boolean isResultNull(int idx, JSONArray fetchedData);
	
}
