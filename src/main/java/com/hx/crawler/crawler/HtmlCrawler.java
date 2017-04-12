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
	@Override
	public Page<HttpResponse> getPage(String url) throws IOException {
		return getPage(url, HtmlCrawlerConfig.get());
	}
	@Override
	public Page<HttpResponse> getPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");

		url = encapQueryStrIfNeeded(url, config);
		Request req = Request.Get(url);
		return doExecute(req, config, config.getProxy());
	}

	// postPage
	@Override
	public Page<HttpResponse> postPage(String url) throws IOException {
		return postPage(url, new HtmlCrawlerConfig());
	}
	@Override
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");

		Request req = encapPostReq(url, config);
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	@Override
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, String bodyData, ContentType contentType) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");
		Tools.assert0(bodyData != null, "bodyData can't be null ");
		Tools.assert0(contentType != null, "contentType can't be null ");

		Request req = encapPostReq(url, config);
		req.bodyString(bodyData, contentType);
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	@Override
	public Page<HttpResponse> postPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config, InputStream inputStream, ContentType contentType) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");
		Tools.assert0(inputStream != null, "inputStream can't be null ");
		Tools.assert0(contentType != null, "contentType can't be null ");

		Request req = encapPostReq(url, config);
		req.bodyStream(inputStream, contentType);
		Response resp = req.execute();
		return new HtmlPage(resp);
	}
	
	// putPage
	@Override
	public Page<HttpResponse> putPage(String url) throws IOException {
		return putPage(url, new HtmlCrawlerConfig());
	}
	@Override
	public Page<HttpResponse> putPage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");

		url = encapQueryStrIfNeeded(url, config);
		Request req = Request.Put(url);
		return doExecute(req, config, config.getProxy());
	}
	
	// deletePage
	@Override
	public Page<HttpResponse> deletePage(String url) throws IOException {
		return deletePage(url, new HtmlCrawlerConfig());
	}
	@Override
	public Page<HttpResponse> deletePage(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) throws IOException {
		Tools.assert0(url != null, "url can't be null ");
		Tools.assert0(config != null, "CrawlerConfig can't be null ");

		url = encapQueryStrIfNeeded(url, config);
		Request req = Request.Delete(url);
		return doExecute(req, config, config.getProxy());
	}
	
	/**
	 * @Description: ��װ����������, ����������, ��������ػ��� [����get, put, delete����]
	 * @param req	����������
	 * @param config ����������Ϣ
	 * @param proxy ������Ϣ
	 * @return
	 * @throws IOException  
	 * @Create at 2017-03-04 22:04:14 by '970655147'
	 */
	private HtmlPage doExecute(Request req, CrawlerConfig<Header, String, NameValuePair, String, String> config, HttpHost proxy) throws IOException {
		req.connectTimeout(config.getTimeout() );
		setHeadersAndCookies(req, config);
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

	/**
	 * ��װpost��request����
	 *
	 * @param url
	 * @param config
	 * @return org.apache.http.client.fluent.Request
	 * @throws
	 * @author 970655147 created at 2017-03-11 15:38
	 */
	private Request encapPostReq(String url, CrawlerConfig<Header, String, NameValuePair, String, String> config) {
		Request req = Request.Post(url);
		req.connectTimeout(config.getTimeout() );
		config(req, config);
		HttpHost proxy = config.getProxy();
		if(proxy != null) {
			req.viaProxy(proxy);
		}

		return req;
	}
	
}