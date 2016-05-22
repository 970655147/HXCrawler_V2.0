/**
 * file name : Crawler.java
 * created at : 3:24:26 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;

import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.util.CrawlerUtils;
import com.hx.log.log.Tools;

// HtmlCrawler
public class HtmlCrawler extends Crawler<HttpResponse, Header, String, NameValuePair, String, String> {

	// getInstance 获取到的唯一实例
	private static HtmlCrawler instance;
	
	// 创建/ 获取一个HtmlCrawler
	public static HtmlCrawler newInstance() {
		return new HtmlCrawler();
	}
	public static HtmlCrawler getInstance() {
		if(instance == null) {
			synchronized (HtmlCrawler.class) {
				if(instance == null) {
					instance = newInstance();
				}
			}
		}
		
		return instance;
	}
	
	// getPage
	public Page<HttpResponse> getPage(String url) throws IOException {
		return getPage(url, new HtmlCrawlerConfig());
	}
	public Page<HttpResponse> getPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) throws IOException {
		Request req = Request.Get(url);
		req.connectTimeout(config.getTimeout() );
		setHeadersAndCookies(req, config);
		
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	
	// postPage
	public Page<HttpResponse> postPage(String url) throws IOException {
		return postPage(url, new HtmlCrawlerConfig());
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) throws IOException {
		Request req = Request.Post(url);
		config(req, config);
		
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, String bodyData, ContentType contentType) throws IOException {
		Request req = Request.Post(url);
		config(req, config);
		req.bodyString(bodyData, contentType);
		
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	
	// 使用crawlerConfig 配置request
	private static void config(Request req, CrawlerConfig<Header, String, NameValuePair, String, String> config) {
		setHeadersAndCookies(req, config);
		req.bodyForm(config.getData() );
	}
	
	// 为request设置请求头 & cookies
	private static void setHeadersAndCookies(Request req, CrawlerConfig<Header, String, NameValuePair, String, String> config) {
		Iterator<Header> it = config.getHeaders().iterator();
//		boolean existCookiesInHeader = false;
		
		while(it.hasNext() ) {
			Header header = it.next();
			if(Tools.equalsIgnoreCase(header.getName(), Tools.COOKIE_STR) ) {
//				header = new BasicHeader(header.getName(), Tools.removeIfEndsWith(header.getValue(), ";") + ";" + Tools.getCookieStr(config.getCookies()) );
//				existCookiesInHeader = true;
				config.addCookies(Tools.getCookiesByCookieStr(header.getValue()) );
				continue ;
			}
			
			req.addHeader(header );
		}
		
		// add "&& ((config.getCookies().size() > 0))" incase of have no cookie		add at 2016.04.07
		// update incase of exists 'COOKIE' in header [can't add 'config.getCookies''s 'Cookie' ]		add at 2016.05.02
		if((config.getCookies().size() > 0) ) {
			req.addHeader(Tools.COOKIE_STR, Tools.getCookieStr(config.getCookies()) );
		}
	}
	
}
