/**
 * file name : CrawlerConfig.java
 * created at : 3:24:46 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.hx.crawler.crawler.interf.CrawlerConfig;

// CrawlerConfig
public class HtmlCrawlerConfig implements CrawlerConfig<Header, String, NameValuePair, String, String> {
	
	// 请求头信息, cookies信息, postData信息
	// 超时配置
	private List<Header> headers;
	private Map<String, String> cookies;
	private List<NameValuePair> data;
	private int timeout;
	
	// 常量
	private static int DEFAULT_TIMEOUT = 10 * 1000;
	private static Map<String, String> DEFAULT_HEADERS = new HashMap<>();
	static {
		DEFAULT_HEADERS.put("Content-Type", "text/html;charset=utf-8");
		DEFAULT_HEADERS.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//		DEFAULT_HEADERS.put("Accept-Encoding", "gzip, deflate, sdch");
//		DEFAULT_HEADERS.put("Accept-Language", "zh-CN,zh;q=0.8");
//		DEFAULT_HEADERS.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	}
	
	// 初始化 添加默认的请求头
	public HtmlCrawlerConfig() {
		headers = new ArrayList<>();
		cookies = new HashMap<>();
		data = new ArrayList<>();
		timeout = DEFAULT_TIMEOUT;
		
		for(Entry<String, String> header : DEFAULT_HEADERS.entrySet()) {
			headers.add(new BasicHeader(header.getKey(), header.getValue()) );
		}
	}
	public HtmlCrawlerConfig(HtmlCrawlerConfig config) {
		this();
		addHeaders(config.getHeaders() );
		addData(config.getData() );
		addCookies(config.getCookies() );
		this.timeout = config.getTimeout();
	}
	
	// getter & setter
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}
	public void setHeaders(Map<String, String> headers) {
		List<Header> headersTmp = new ArrayList<>(headers.size() );
		for(Map.Entry<String, String> entry : headers.entrySet() ) {
			headersTmp.add(new BasicHeader(entry.getKey(), entry.getValue()) );
		}
		this.headers = headersTmp;
	}
	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}
	public void setData(List<NameValuePair> data) {
		this.data = data;
	}
	public void setData(Map<String, String> data) {
		List<NameValuePair> dataTmp = new ArrayList<>(data.size() );
		for(Map.Entry<String, String> entry : data.entrySet() ) {
			dataTmp.add(new BasicNameValuePair(entry.getKey(), entry.getValue()) );
		}
		this.data = dataTmp;
	}
	public int getTimeout() {
		return timeout;
	}
	public List<Header> getHeaders() {
		return headers;
	}
	public Map<String, String> getCookies() {
		return cookies;
	}
	public List<NameValuePair> getData() {
		return data;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public HtmlCrawlerConfig addHeader(String key, String value) {
		int idx = indexOfHeader(headers, key);
		if(idx >= 0) {
			headers.remove(idx);
		}
		headers.add(new BasicHeader(key, value));
		
		return this;
	}
	public HtmlCrawlerConfig addHeaders(List<Header> headers) {
		for(Header header : headers ) {
			addHeader(header.getName(), header.getValue() );
		}
		
		return this;
	}
	public HtmlCrawlerConfig addCookie(String key, String value) {
		cookies.put(key, value);
		return this;
	}
	public HtmlCrawlerConfig addCookies(Map<String, String> headers) {
		for(Entry<String, String> header : headers.entrySet() ) {
			addCookie(header.getKey(), header.getValue() );
		}
		
		return this;
	}
	public HtmlCrawlerConfig addData(String key, String value) {
		int idx = indexOfData(data, key);
		if(idx >= 0) {
			data.remove(idx);
		}
		data.add(new BasicNameValuePair(key, value) );
		
		return this;
	}
	public HtmlCrawlerConfig addData(List<NameValuePair> datas) {
		for(NameValuePair data : datas ) {
			addData(data.getName(), data.getValue() );
		}
		
		return this;
	}
	public HtmlCrawlerConfig addData(Map<String, String> datas) {
		for(Entry<String, String> data : datas.entrySet() ) {
			addData(data.getKey(), data.getValue() );
		}
		
		return this;
	}
	
	// key 在headers, data中的索引
	static int indexOfHeader(List<Header> headers, String key) {
		for(int i=0; i<headers.size(); i++) {
			if(headers.get(i).getName().equals(key) ) {
				return i;
			}
		}
		
		return -1;
	}
	static int indexOfData(List<NameValuePair> data, String key) {
		for(int i=0; i<data.size(); i++) {
			if(data.get(i).getName().equals(key) ) {
				return i;
			}
		}
		
		return -1;
	}
	
}
