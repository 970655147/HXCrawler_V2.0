/**
 * file name : Crawler.java
 * created at : 3:24:26 PM Jul 26, 2015
 * created by 970655147
 */

package com.hx.crawler.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.Page;
import com.hx.log.util.Tools;

// HtmlCrawler
public class HtmlCrawler extends Crawler<HttpResponse, Header, String, NameValuePair, String, String> {

	// getInstance ��ȡ����Ψһʵ��
	private static HtmlCrawler instance;
	
	// ����/ ��ȡһ��HtmlCrawler
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
		return getPage(url, config, null);
	}
	public Page<HttpResponse> getPage(String url, HttpHost proxy) throws IOException {
		return getPage(url, new HtmlCrawlerConfig(), proxy);
	}
	public Page<HttpResponse> getPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, HttpHost proxy) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");
		
		url = encapQueryStrIfNeeded(url, config);
		Request req = Request.Get(url);
		req.connectTimeout(config.getTimeout() );
		setHeadersAndCookies(req, config);
		if(proxy != null) {
			req.viaProxy(proxy);
		}
		
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	
	// postPage
	public Page<HttpResponse> postPage(String url) throws IOException {
		return postPage(url, new HtmlCrawlerConfig());
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) throws IOException {
		return postPage(url, config, null);
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, String bodyData, ContentType contentType) throws IOException {
		return postPage(url, config, bodyData, contentType, null);
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, InputStream inputStream, ContentType contentType) throws IOException {
		return postPage(url, config, inputStream, contentType, null);
	}
	public Page<HttpResponse> postPage(String url, HttpHost proxy) throws IOException {
		return postPage(url, new HtmlCrawlerConfig(), proxy);
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, HttpHost proxy) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");
		
		Request req = Request.Post(url);
		req.connectTimeout(config.getTimeout() );
		config(req, config);
		if(proxy != null) {
			req.viaProxy(proxy);
		}
		
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, String bodyData, ContentType contentType, HttpHost proxy) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");
		Tools.assert0(bodyData != null, "bodyData can't be null ");
		Tools.assert0(contentType != null, "contentType can't be null ");
		
		Request req = Request.Post(url);
		req.connectTimeout(config.getTimeout() );
		config(req, config);
		req.bodyString(bodyData, contentType);
		if(proxy != null) {
			req.viaProxy(proxy);
		}
		
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, InputStream inputStream, ContentType contentType, HttpHost proxy) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");
		Tools.assert0(inputStream != null, "inputStream can't be null ");
		Tools.assert0(contentType != null, "contentType can't be null ");
		
		Request req = Request.Post(url);
		req.connectTimeout(config.getTimeout() );
		config(req, config);
		req.bodyStream(inputStream, contentType);
		if(proxy != null) {
			req.viaProxy(proxy);
		}
		
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	
	// ʹ��crawlerConfig ����request
	private static void config(Request req, CrawlerConfig<Header, String, NameValuePair, String, String> config) {
		setHeadersAndCookies(req, config);
		req.bodyForm(config.getData() );
	}
	// Ϊrequest��������ͷ & cookies
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
	// ��������List<NameValuePair> ת��ΪMap<Name, Value>
	private Map<String, String> data2Map(List<NameValuePair> data) {
		if(Tools.isEmpty(data) ) {
			return Collections.emptyMap();
		}
		
		Map<String, String> result = new LinkedHashMap(Tools.estimateMapSize(data.size()) );
		for(NameValuePair pair : data) {
			result.put(pair.getName(), pair.getValue() );
		}
		return result;
	}
	// url �Ͳ�ѯ�ַ����ķָ���
	public static final String URL_QUERYSTR_SEP = "?";
	// �����Ҫ��װ���ݵ���ѯ�ַ���, ������ƴ�ӵ�url����
	private String encapQueryStrIfNeeded(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) {
		String queryStr = Tools.encapQueryString(data2Map(config.getData()) );
		StringBuilder sb = new StringBuilder(url.length() + queryStr.length() + URL_QUERYSTR_SEP.length() );
		
		sb.append(url);
		if(! Tools.isEmpty(queryStr) && (! url.contains(URL_QUERYSTR_SEP)) ) {
			sb.append(URL_QUERYSTR_SEP);
		}
		sb.append(queryStr);
		return sb.toString();
	}
	
}
