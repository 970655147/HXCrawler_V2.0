/**
 * file name : Tools.java
 * created at : 6:58:34 PM Jul 25, 2015
 * created by 970655147
 */

package com.hx.crawler.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.xml.sax.InputSource;

import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.SingleUrlTask;
import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.ScriptParameter;
import com.hx.crawler.xpathParser.XPathParser;
import com.hx.crawler.xpathParser.interf.ResultJudger;
import com.hx.log.log.Log;
import com.hx.log.log.LogPattern.LogPatternChain;
import com.hx.log.log.LogPattern.LogPatternType;
import com.hx.log.log.Tools;

// ������
public class CrawlerUtils {
	
	// parse ��س���
	public static final String PARSE_METHOD_NAME = "parse";
	public static final boolean IS_PARSE_METHOD_STATIC = true;
	public static final Class[] PARSE_METHOD_PARAMTYPES = new Class[]{ScriptParameter.class };

	// ----------------- crawler ������� -----------------------
	// ��ȡ�����֮����ĵ�
	public static void getPreparedDoc(String url, String file) throws Exception {
		Tools.assert0(url != null, "'url' can't be null ");
		String html = HtmlCrawler.newInstance().getPage(url).getContent();
		getPreparedDoc(url, html, file);
	}
	public static void getPreparedDoc(String url, String html, String file) throws Exception {
		Tools.assert0(html != null, "'html' can't be null ");
		Tools.assert0(file != null, "'file' can't be null ");
		Tools.save(StringEscapeUtils.unescapeHtml(CrawlerUtils.normalize(html) ), file);
	}
	
	public final static com.hx.crawler.xpathParser.interf.Parser xpathParser = new XPathParser();
	// ͨ��xpath ��ȡ���
	public static JSONArray getResultByXPath(String html, String url, String xpath) throws Exception {
		Tools.assert0(html != null, "'html' can't be null ");
		Tools.assert0(xpath != null, "'xpath' can't be null ");
		return xpathParser.parse(CrawlerUtils.normalize(html), url, xpath);
	}
	public static JSONArray getResultByXPathes(String html, String url, String[] xpathes, ResultJudger judger) throws Exception {
		Tools.assert0(html != null, "'html' can't be null ");
		Tools.assert0(xpathes != null, "'xpathes' can't be null ");
		Tools.assert0(judger != null, "'judger' can't be null ");
		
		for(int i=0; i<xpathes.length; i++) {			
			JSONArray res = getResultByXPath(html, url, xpathes[i]);
			if(! judger.isResultNull(i, res)) {
				return res;
			}
		}
		
		return null;
	}
	
	// ��ʽ��html [ȥ��, ��� ���淶�ı�ǩ]
	// ˼· : ʹ��tagSoup�淶��xml
		// ע�� : ��Ҫ�ض���ContentHandler�����
		// ��Ҫȥ��xml����, ��Ҫȥ��html��ǩ��xmlns������
	public static String normalize(String html) throws Exception {
		Tools.assert0(html != null, "'html' can't be null ");
//		StringReader xmlReader = new StringReader("");
		StringReader sr = new StringReader(html);
		Parser parser = new Parser();		//ʵ����Parse
		
//		// �����ʽ��֮���xml
		XMLWriter writer = new XMLWriter();	//ʵ����XMLWriter����SAX���ݴ�����
		parser.setContentHandler(writer);	//�������ݴ�����
		StringWriter strWriter = new StringWriter();
		writer.setOutput(strWriter);
		
		parser.parse(new InputSource(sr));	//����
		
//		Scanner scan = new PYXScanner();
//		scan.scan(xmlReader, parser);	//ͨ��xmlReader��ȡ������Ľ��
		
//		char[] buff = new char[1024];
//		StringBuilder sb = new StringBuilder();
//		while(xmlReader.read(buff) != -1) {
//		    sb.append(buff);		//��ӡ������Ľṹ���õ�HTML�ĵ�
//		} 
		
		// ��ȡ�ṹ���õ�html, ��ȥ��xml����
		String res = strWriter.toString();
		res = res.substring(res.indexOf("\n") + 1);
		
		// ������Ϊ�������xmlns������, �������ҵ�xPath��ȡ���������⡣����, ����˵ ֻ��ͨ��/*[@XX='XXX'] ����ȡ����
		// ���Ҷ�ȡ�������� ����һ��xmlns������...						--2015.07.31
//		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><strong><span id=\"ctl01_ContentPlaceHolder1_lblPriceTitle\">ProductPricing</span>:</strong></body></html>";
		// ���� �����res����ͳһ�Ĵ���, ȥ��html��ǩ��xmlns������
		String needReplace = "xmlns=\"http://www.w3.org/1999/xhtml\"";
		return res.replace(needReplace, Tools.EMPTY_STR);
	}
	
	// ����һ��ScriptParameter
	public static SingleUrlTask newSingleUrlTask(Crawler<HttpResponse, Header, String, NameValuePair, String, String> crawler, String url, Map<String, Object>param) {
		SingleUrlTask res = new SingleUrlTask();
		res.setCrawler(crawler);
		res.setUrl(url);
		res.setParam(param);
		crawler.setScriptParameter(res);
		res.setTaskGroupId(110);
		res.setTaskId(120);
		
		return res;
	}
	
	// ͨ��xpath��ȡ��ʵ��xpath
	// ���Ը�дΪJSONArray.toString() ʵ�� [˼·����'duncen'[newEggͬ��] ]
	public static String getRealXPathByXPathObj(String xpath) {
		Tools.assert0(xpath != null, "'xpath' can't be null ");
		return new JSONArray().element(xpath).toString();
	}
	public static String getRealXPathByXPathObj(String... xpathes) {
		JSONArray res = new JSONArray();
		for(String xpath : xpathes) {
			res.add(xpath);
		}
		
		return res.toString();
	}
	
	// ���÷������ָ����class��methodName����
	public static void parse(String className, String url, Map<String, Object> params) throws Exception {
		parse(className, url, params, PARSE_METHOD_NAME, IS_PARSE_METHOD_STATIC, PARSE_METHOD_PARAMTYPES);
	}
	public static void parse(String className, String url, Map<String, Object> params, String methodName, boolean isStaticMethod, Class[] methodParamTypes) throws Exception {
		Tools.assert0(className != null, "'className' can't be null ");
		Tools.assert0(url != null, "'url' can't be null ");
		Tools.assert0(methodName != null, "'methodName' can't be null ");
		
		Class clazz = Class.forName(className);
		Method method = clazz.getMethod(methodName, methodParamTypes);
		SingleUrlTask singleUrlTask = newSingleUrlTask(HtmlCrawler.newInstance(), url, params);
		
		try {
			if(isStaticMethod) {
				method.invoke(null, singleUrlTask);
			} else {
				method.invoke(clazz.newInstance(), singleUrlTask);
			}
		} catch(Exception e) {
			logErrorMsg(singleUrlTask, e);
		}
	}
	public static void parseAsync(final String className, final String url, final Map<String, Object> params) throws Exception {
		parseAsync(className, url, params, PARSE_METHOD_NAME, IS_PARSE_METHOD_STATIC, PARSE_METHOD_PARAMTYPES);
	}
	public static void parseAsync(final String className, final String url, final Map<String, Object> params, final String methodName, final boolean isStaticMethod, final Class[] methodParamTypes) throws Exception {
		Tools.execute(new Runnable() {
			public void run() {
				try {
					parse(className, url, params, methodName, isStaticMethod, methodParamTypes);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}	
	
	// ΪnextStageParams���category
	public static void addNameUrlSite(JSONObject category, JSONObject nextStageParams) {
		Tools.assert0(! Tools.isEmpty(category), "'category' can't be null ");
		nextStageParams.put(Tools.NAME, category.getString(Tools.NAME) );
		nextStageParams.put(Tools.URL, category.getString(Tools.URL) );
		nextStageParams.put(Tools.SITE, nextStageParams.getString(Tools.SITE) + "." + category.getString(Tools.NAME) );
	}

	// ��ȡtaskName
	public static String getTaskName(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask) {
		Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
		return "crawl " + singleUrlTask.getParam().get(Tools.TASK) + " from " + singleUrlTask.getParam().get(Tools.SITE);
	}
	
	// ------------ ��־��� --------------------
	// ��ص�logPattern
	public static LogPatternChain taskBeforeLogPatternChain = com.hx.log.log.Constants.taskBeforeLogPatternChain;
	public static LogPatternChain taskAfterLogPatternChain = com.hx.log.log.Constants.taskAfterLogPatternChain;
	public static LogPatternChain taskExceptionLogPatternChain = com.hx.log.log.Constants.taskExceptionLogPatternChain;
	// ��ӡ�������־��Ϣ
	public static void logBeforeTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, boolean debugEnable) {
		Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
		if(debugEnable ) {
			String info = com.hx.log.log.Constants.formatLogInfo(taskBeforeLogPatternChain, new JSONObject()
			.element(LogPatternType.URL.typeKey(), singleUrlTask.getUrl()).element(LogPatternType.TASK_NAME.typeKey(), CrawlerUtils.getTaskName(singleUrlTask))
			.element(LogPatternType.MODE.typeKey(), com.hx.log.log.Constants.LOG_MODES[com.hx.log.log.Constants.OUT_IDX])
			);
			Log.log(info );
		}
	}
	public static void logBeforeTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String debugEnable) {
		logBeforeTask(singleUrlTask, Boolean.parseBoolean(debugEnable) );
	}
	public static void logAfterTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String fetchedResult, String spent, boolean debugEnable) {
		Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
		if(debugEnable ) {
			String info = com.hx.log.log.Constants.formatLogInfo(taskAfterLogPatternChain, new JSONObject()
							.element(LogPatternType.RESULT.typeKey(), fetchedResult).element(LogPatternType.TASK_NAME.typeKey(), CrawlerUtils.getTaskName(singleUrlTask))
							.element(LogPatternType.SPENT.typeKey(), spent).element(LogPatternType.MODE.typeKey(), com.hx.log.log.Constants.LOG_MODES[com.hx.log.log.Constants.OUT_IDX])
							);
		    Log.log(info );
		}
	}
	public static void logAfterTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String fetchedResult, String spent, String debugEnable) {
		logAfterTask(singleUrlTask, fetchedResult, spent, Boolean.parseBoolean(debugEnable) );
	}
	public static void logErrorMsg(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, Exception e) {
		Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
		String info = com.hx.log.log.Constants.formatLogInfo(taskExceptionLogPatternChain, new JSONObject()
						.element(LogPatternType.EXCEPTION.typeKey(), e.getClass().getName() + " : " + e.getMessage() )
						.element(LogPatternType.TASK_NAME.typeKey(), CrawlerUtils.getTaskName(singleUrlTask))
						.element(LogPatternType.URL.typeKey(), singleUrlTask.getUrl()).element(LogPatternType.MODE.typeKey(), com.hx.log.log.Constants.LOG_MODES[com.hx.log.log.Constants.ERR_IDX])
						);
		Log.err(info );
	}
   // ------------ ���� --------------------

	
}
