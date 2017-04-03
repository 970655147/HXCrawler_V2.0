/**
 * file name : CrawlerConfig.java
 * created at : 11:24:46 PM Apr 09, 2016
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;

// CrawlerConfig
public interface CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> {

	// getter & setter
	public void setHeaders(List<HeaderType> headers);
	public void setHeaders(Map<String, HeaderValType> headers);
	public void setData(List<DataType> data);
	public void setData(Map<String, DataValType> data);
	public void setCookies(Map<String, CookieValType> cookies);
	public void setTimeout(int timeout);
	public void setProxy(HttpHost proxy);
	
	public List<HeaderType> getHeaders();
	public Map<String, CookieValType> getCookies();
	public List<DataType> getData();
	public int getTimeout();
	public HttpHost getProxy();
	
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addHeader(String key, HeaderValType value);
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addHeaders(List<HeaderType> headers);
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addHeaders(Map<String, HeaderValType> headers);
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addCookie(String key, CookieValType value);
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addCookies(Map<String, CookieValType> cookies);
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addData(String key, DataValType value);
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addData(List<DataType> datas);
	public CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> addData(Map<String, DataValType> datas);
	
}
