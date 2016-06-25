/**
 * file name : Crawler.java
 * created at : 8:23:27 PM Jul 31, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;

// Cralwer ¹æ·¶
public abstract class Crawler<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> {

	// url²ÎÊý
	protected ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> scriptParameter;
	
	// getPage
	public abstract Page<ResponseType> getPage(String url) throws IOException;
	public abstract Page<ResponseType> getPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config) throws IOException;
	// add at 2016.06.25
	public abstract Page<ResponseType> getPage(String url, HttpHost proxy) throws IOException;
	public abstract Page<ResponseType> getPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config, HttpHost proxy) throws IOException;
	
	// postPage
	public abstract Page<ResponseType> postPage(String url) throws IOException;
	public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config) throws IOException;
	public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config, String bodyData, ContentType contentType) throws IOException;
	// add at 2016.06.02
	public abstract Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, InputStream inputStream, ContentType contentType) throws IOException;
	//  add at 2016.06.25
	public abstract Page<ResponseType> postPage(String url, HttpHost proxy) throws IOException;
	public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config, HttpHost proxy) throws IOException;
	public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config, String bodyData, ContentType contentType, HttpHost proxy) throws IOException;
	public abstract Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, InputStream inputStream, ContentType contentType, HttpHost proxy) throws IOException;
		
	// setter & getter
	public ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> getScriptParameter() {
		return scriptParameter;
	}
	public void setScriptParameter(ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> scriptParameter) {
		this.scriptParameter = scriptParameter;
	}
	
	// ..
	
}
