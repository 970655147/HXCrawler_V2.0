/**
 * file name : Page.java
 * created at : 11:32:42 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import java.util.Map;

// Page
public interface Page<ResponseType> {

	// setter & getter
	public void setCharset(String charset);
	public ResponseType getResponse();
	public String getCharset();
	public Map<String, String> getCookies();
	// 获取Page的内容
	public String getContent();

}
