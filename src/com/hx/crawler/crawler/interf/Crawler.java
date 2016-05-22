/**
 * file name : Crawler.java
 * created at : 8:23:27 PM Jul 31, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler.interf;

import java.io.IOException;

import org.apache.http.entity.ContentType;

// Cralwer ¹æ·¶
public abstract class Crawler<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> {

	// url²ÎÊý
	protected ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> scriptParameter;
	
	// getPage
	public abstract Page<ResponseType> getPage(String url) throws IOException;
	public abstract Page<ResponseType> getPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config) throws IOException;
	
	// postPage
	public abstract Page<ResponseType> postPage(String url) throws IOException;
	public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config) throws IOException;
	public abstract Page<ResponseType> postPage(String url, CrawlerConfig<HeaderType, HeaderValType, DataType, DataValType, CookieValType> config, String bodyData, ContentType contentType) throws IOException;
	
	// setter & getter
	public ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> getScriptParameter() {
		return scriptParameter;
	}
	public void setScriptParameter(ScriptParameter<ResponseType, HeaderType, HeaderValType, DataType, DataValType, CookieValType> scriptParameter) {
		this.scriptParameter = scriptParameter;
	}
	
	// ..
	
}
