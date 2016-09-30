/**
 * file name : Tools.java
 * created at : 6:58:34 PM Jul 25, 2015
 * created by 970655147
 */

package com.hx.crawler.util;

import static com.hx.log.util.Log.err;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.xml.sax.InputSource;

import com.hx.attrHandler.attrHandler.interf.AttrHandler;
import com.hx.attrHandler.util.AttrHandlerUtils;
import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.SingleUrlTask;
import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.crawler.interf.ScriptParameter;
import com.hx.crawler.xpathParser.XPathParser;
import com.hx.crawler.xpathParser.interf.ResultJudger;
import com.hx.log.util.Constants;
import com.hx.log.util.JSONExtractor;
import com.hx.log.util.Log;
import com.hx.log.util.LogPattern.LogPatternChain;
import com.hx.log.util.LogPattern.LogPatternType;
import com.hx.log.util.LogPatternUtils;
import com.hx.log.util.Tools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

// ������
public final class CrawlerUtils {
	
	// disable constructor
	private CrawlerUtils() {
		Tools.assert0("can't instantiate !");
	}
	
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
	public static LogPatternChain taskBeforeLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_BEFORE_LOG_PATTERN) );
	public static LogPatternChain taskAfterLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_AFTER_LOG_PATTERN) );
	public static LogPatternChain taskExceptionLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_EXCEPTION_LOG_PATTERN) );
	// ��ӡ�������־��Ϣ
	public static void logBeforeTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, boolean debugEnable) {
		Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
		if(debugEnable ) {
			String info = LogPatternUtils.formatLogInfo(taskBeforeLogPatternChain, new JSONObject()
				.element(Constants.LOG_PATTERN_URL, singleUrlTask.getUrl()).element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
				.element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.OUT_IDX])
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
			String info = LogPatternUtils.formatLogInfo(taskAfterLogPatternChain, new JSONObject()
								.element(Constants.LOG_PATTERN_RESULT, fetchedResult).element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
								.element(Constants.LOG_PATTERN_SPENT, spent).element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.OUT_IDX])
							);
		    Log.log(info );
		}
	}
	public static void logAfterTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String fetchedResult, String spent, String debugEnable) {
		logAfterTask(singleUrlTask, fetchedResult, spent, Boolean.parseBoolean(debugEnable) );
	}
	public static void logErrorMsg(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, Exception e) {
		Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
		String info = LogPatternUtils.formatLogInfo(taskExceptionLogPatternChain, new JSONObject()
							.element(Constants.LOG_PATTERN_EXCEPTION, e.getClass().getName() + " : " + e.getMessage() )
							.element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
							.element(Constants.LOG_PATTERN_URL, singleUrlTask.getUrl()).element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.ERR_IDX])
						);
		Log.err(info );
	}
	
	// ------------ PipelineTask���  add at 2016.08.13 --------------------
	// PipelineTask��س���
	public static final String URL = "url";
	public static final String METHOD = "method";
	public static final String XPATHES = "xpathes";
	public static final String JUDGER = "judger";
	public static final String START_UP = "startUp";
	public static final String TASK_PARAM = "taskParam";
	public static final String CRAWLER_CONFIG = "crawlerConfig";
	public static final String CLEAR_PREV_HEADERS = "clearPrevHeaders";
	public static final String CLEAR_PREV_COOKIES = "clearPrevCookies";
	public static final String CLEAR_PREV_DATA = "clearPrevData";
	public static final String HEADERS = "headers";
	public static final String COOKIES = "cookies";
	public static final String DATA = "data";
	public static final String TIMEOUT = "timeout";
	public static final String DEBUG_ENABLE = "debugEnable";
	public static final String SAVE_PREPARE_DOC = "savePreparedDoc";
	public static final String SAVE_FETCHED_RESULT = "saveFetchedResult";
	public static final String EXTRACT_FETCHED_RESULT = "extractFetchedResult";
	public static final String FETCHED_RESULT_MAPPER = "fetchedResultMapper";
	public static final String NEXT_STAGE_PARSE_ASYNC = "nextStageParseAsync";
	public static final String NEXT_STAGE_URL_PATTERN = "nextStageUrlPat";
	public static final String NEXT_STAGE_PARAM_PATTERN = "nextStageParamPat";
	
	// ��������
	public static String SAVE_PREPARED_DOC_PATH = Tools.getTmpPath("preparedDoc", Tools.HTML);
	public static String SAVE_FETCHED_RESULT_PATH = Tools.getTmpPath("fetchedResult", Tools.TXT);
	public static final String DO_PIPELINE_TASK_NAME = "doPipelineTask";
	
	// ��ȡ������configPath��Ӧ������, Ȼ�� ִ����ˮ��ʽ��ִ������
	public static void doPipelineTaskAsync(String configPath) throws Exception {
		doPipelineTask0(configPath, null, null, true);
	}
	public static void doPipelineTaskAsync(String configPath, SingleUrlTask scriptParameter) throws Exception {
		doPipelineTask0(configPath, scriptParameter, null, true);
	}
	public static void doPipelineTaskAsync(String configPath, HtmlCrawlerConfig crawlerConfig) throws Exception {
		doPipelineTask0(configPath, null, crawlerConfig, true);
	}
	public static void doPipelineTaskAsync(String configPath, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
		doPipelineTask0(configPath, scriptParameter, crawlerConfig, true);
	}
	public static void doPipelineTaskAsync(JSONArray globalConfig) throws Exception {
		doPipelineTask0(globalConfig, null, null, true);
	}
	public static void doPipelineTaskAsync(JSONArray globalConfig, SingleUrlTask scriptParameter) throws Exception {
		doPipelineTask0(globalConfig, scriptParameter, null, true);
	}
	public static void doPipelineTaskAsync(JSONArray globalConfig, HtmlCrawlerConfig crawlerConfig) throws Exception {
		doPipelineTask0(globalConfig, null, crawlerConfig, true);
	}
	public static void doPipelineTaskAsync(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
		doPipelineTask0(globalConfig, scriptParameter, crawlerConfig, true);
	}
	
	public static JSONArray doPipelineTask(String configPath) throws Exception {
		return doPipelineTask0(configPath, null, null, false);
	}
	public static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter) throws Exception {
		return doPipelineTask0(configPath, scriptParameter, null, false);
	}
	public static JSONArray doPipelineTask(String configPath, HtmlCrawlerConfig crawlerConfig) throws Exception {
		return doPipelineTask0(configPath, null, crawlerConfig, false);
	}
	public static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
		return doPipelineTask0(configPath, scriptParameter, crawlerConfig, false);
	}
	public static JSONArray doPipelineTask(JSONArray globalConfig) throws Exception {
		return doPipelineTask0(globalConfig, null, null, false);
	}
	public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter) throws Exception {
		return doPipelineTask0(globalConfig, scriptParameter, null, false);
	}
	public static JSONArray doPipelineTask(JSONArray globalConfig, HtmlCrawlerConfig crawlerConfig) throws Exception {
		return doPipelineTask0(globalConfig, null, crawlerConfig, false);
	}
	public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
		return doPipelineTask0(globalConfig, scriptParameter, crawlerConfig, false);
	}
	
	private static JSONArray doPipelineTask0(String configPath, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
		JSONArray globalConfig = JSONArray.fromObject(Tools.getContent(configPath) );
		return doPipelineTask0(globalConfig, scriptParameter, crawlerConfig, async);
	}
	/**
	 * @Name: doPipelineTask0 
	 * @Description: ��ˮ��ʽ��ִ�и��������õ�����
	 * 	����globalConfig, �ҵ�startUpΪtrue������, �Ӹ�����ʼִ��
	 * 	��ʼ��scriptParameter, crawlerConfig, Ȼ������Ƿ��첽ִ������ ����ʹ�õ�ǰ�߳�ִ������, ����ʹ��Tools�е��̳߳�ִ������
	 * 	������첽ִ������, resΪnull, ���� resΪ���������񷵻صĽ��
	 * @param globalConfig һ��һ������������
	 * @param scriptParameter scriptParameter[ͨ��startUp��������г�ʼ��]
	 * @param crawlerConfig crawlerConfig[ͨ��startUp��������г�ʼ��]
	 * @param async �Ƿ��첽ִ��
	 * @return
	 * @throws Exception  
	 * @Create at 2016-09-30 20:02:54 by '970655147'
	 */
	private static JSONArray doPipelineTask0(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
		Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");
		
		JSONArray res = null;
		for(int i=0, len=globalConfig.size(); i<len; i++) {
			JSONObject stageConfig = globalConfig.getJSONObject(i);
			if(stageConfig.optBoolean(START_UP) ) {
				HtmlCrawlerConfig nextStageCrawlerConfig = crawlerConfig;
				if(crawlerConfig == null) {
					nextStageCrawlerConfig = HtmlCrawlerConfig.get();
				}
				encapCrawlerConfig(stageConfig.optJSONObject(CRAWLER_CONFIG), nextStageCrawlerConfig);
				SingleUrlTask nextStageScriptParameter = scriptParameter;
				if(scriptParameter == null) {
					Crawler crawler = HtmlCrawler.getInstance();
					nextStageScriptParameter = CrawlerUtils.newSingleUrlTask(crawler, stageConfig.optString(URL), stageConfig.optJSONObject(TASK_PARAM) );
				} else {
					nextStageScriptParameter.addParam(stageConfig.optJSONObject(TASK_PARAM) );
				}
				
				if(async) {
					Runnable task = new CrawlerPipelineTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, i);
					Tools.execute(task);
				} else {
					res = doPipelineParse(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, i);
				}
				break ;
			}
		}
		
		// user's program do 
//		if(async) {
//			Tools.awaitShutdown();
//		}
//		Tools.closeAnBuffer(DO_PIPELINE_TASK_NAME);
		return res;
	}
	
	/**
	 * @Name: doPipelineParse 
	 * @Description: ��ˮ��ʽ��ִ�и���������
	 * 	У��scriptParameter, crawerConfig, globalConfig, ��β��֦
	 * 	��ȡ��ǰ���������, ��У��
	 * 	�Ƿ���Ҫ��ӡ��־, ��ӡץȡ����ʼ����־
	 * 	��ȡ��ǰ�����xpathes, Ȼ��������õ������������get, post����, У���ȡ���Ľ��, �����Ҫ����ץȡ���ĵ�, �򱣴��ĵ�
	 * 	Ȼ�� ����xpathes�����ĵ�, ���������õ�JUDGERȷ��"�ؼ�������"����[����JUDGER����ResultJudger]
	 * 	У����, �����Ҫ������, �򱣴���
	 * 
	 * 	������н�������Ҫִ�е�����, ���fetchedResult�л�ȡ��һ�������url(��), Ȼ��ץȡ��Ҫ�Ĳ���, ��װscriptParameter, �����Ƿ��첽ִ������, ������ʹ�õ�ǰ�߳�ִ������, ����ʹ��Tools�̳߳��е��߳�ִ������
	 * @param scriptParameter 
	 * @param crawlerConfig 
	 * @param globalConfig
	 * @param stageId ��ǰ������������һ��stage
	 * @return ���ص�ǰ����ץȡ���Ľ��
	 * @throws Exception  
	 * @Create at 2016-09-30 20:08:18 by '970655147'
	 */
	public static JSONArray doPipelineParse(SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig, JSONArray globalConfig, int stageId) throws Exception {
		Tools.assert0(scriptParameter != null, "'scriptParameter' can't be null ");
		Tools.assert0(crawlerConfig != null, "'crawlerConfig' can't be null ");
		Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");
		
		// prepare
		if(stageId >= globalConfig.size() ) {
			// after finalStage
			return null;
		}
		final JSONObject config = globalConfig.getJSONObject(stageId);
		if(Tools.isEmpty(config) ) {
			err("got empty config while stageId : '" + stageId + "'");
			return null;
		}
		Crawler crawler = scriptParameter.getCrawler();
		String url = scriptParameter.getUrl();
		boolean debugEnable = Boolean.valueOf(scriptParameter.getParam(Tools.DEBUG_ENABLE).toString() );
		
		// encap xpathes
		JSONArray xpathesFromConfig = config.getJSONArray(XPATHES);
		String[] xpathes = new String[xpathesFromConfig.size()];
		for(int i=0, len=xpathesFromConfig.size(); i<len; i++) {
			xpathes[i] = xpathesFromConfig.getString(i);
		}
		
		long start = System.currentTimeMillis();
		CrawlerUtils.logBeforeTask(scriptParameter, debugEnable);
		
		// encap config, post requests
		encapCrawlerConfig(config.optJSONObject(CRAWLER_CONFIG), crawlerConfig);
		Page<HttpResponse> page = null;
		if(Tools.GET.equalsIgnoreCase(config.optString(METHOD)) ) {
			page = crawler.getPage(url, crawlerConfig);
		} else if(Tools.POST.equalsIgnoreCase(config.optString(METHOD)) ) {
			page = crawler.postPage(url, crawlerConfig);
		}
		if(page == null) {
			err("got empty page while fetch : '" + url + "'");
			return null;
		}
		
		// get result by xpathes
		String html = page.getContent();
		if(config.optBoolean(SAVE_PREPARE_DOC) ) {
			CrawlerUtils.getPreparedDoc(url, html, SAVE_PREPARED_DOC_PATH);
		}
		JSONArray fetchedResult = CrawlerUtils.getResultByXPathes(html, url, xpathes, new ResultJudger() {
			@Override
			public boolean isResultNull(int idx, JSONArray fetchedData) {
				JSONArray judgersConfig = config.optJSONArray(JUDGER);
				int gotResNum = -1;
				for(Object _judger : judgersConfig) {
					String judgerPat = _judger.toString();
					if(! Tools.isEmpty(judgerPat) ) {
						JSONArray arr = JSONExtractor.extractInfoFromJSON(fetchedData, judgerPat);
						if(Tools.isEmpty(arr) ) {
							return true;
						}
						
						if(gotResNum < 0) {
							gotResNum = arr.size();
						} else {
							if(gotResNum != arr.size() ) {
								return true;
							}
						}
					}
				}
				
				return false;
			}
		});
		
		long spent = System.currentTimeMillis() - start;
		CrawlerUtils.logAfterTask(scriptParameter, String.valueOf(fetchedResult), String.valueOf(spent), debugEnable);
		
		if(fetchedResult == null) {
			err("fetched nothing with url : '" + url + "'");
			return null;
		}
		
		if(config.optBoolean(SAVE_FETCHED_RESULT) ) {
//			Tools.append(String.valueOf(fetchedResult), SAVE_FETCHED_RESULT_PATH);
			JSONArray extractedFetchedResult = fetchedResult;
			if(! Tools.isEmpty(config.optString(EXTRACT_FETCHED_RESULT)) ) {
				extractedFetchedResult = JSONExtractor.extractInfoFromJSON(extractedFetchedResult, config.optString(EXTRACT_FETCHED_RESULT) );
			}
			
			String saveFetchedResult = String.valueOf(extractedFetchedResult);
			if(! Tools.isEmpty(config.optString(FETCHED_RESULT_MAPPER)) ) {
				AttrHandler handler = AttrHandlerUtils.handlerParse(config.optString(FETCHED_RESULT_MAPPER), Constants.HANDLER);
				saveFetchedResult = handler.handle(String.valueOf(saveFetchedResult) );
			}
			Tools.appendBufferCRLF(DO_PIPELINE_TASK_NAME, String.valueOf(saveFetchedResult) );
		}
		
		// process next stage
		if(stageId < globalConfig.size() - 1) {
			String nextStageUrlPat = config.optString(NEXT_STAGE_URL_PATTERN);
			JSONObject nextStageUrlParam = config.optJSONObject(NEXT_STAGE_PARAM_PATTERN);
			boolean parseAsync = config.optBoolean(NEXT_STAGE_PARSE_ASYNC);
			
			if(! Tools.isEmpty(nextStageUrlPat) ) {
				JSONArray nextStageUrls = JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlPat);
				JSONObject nextStageParams = new JSONObject();
				for(Object _key : nextStageUrlParam.names() ) {
					String key = (String) _key;
					if(! Tools.isEmpty(key) ) {
						nextStageParams.element(key, JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlParam.getString(key)) );
					}
				}
				
				for(int i=0; i<nextStageUrls.size(); i++) {
					String nextStageUrl = (String) nextStageUrls.getString(i);
					JSONObject nextStageConfig = globalConfig.getJSONObject(stageId + 1);
					HtmlCrawlerConfig nextStageCrawlerConfig = new HtmlCrawlerConfig(crawlerConfig);
					// duplicate, rm
					// encapCrawlerConfig(nextStageConfig.optJSONObject(CRAWLER_CONFIG), nextStageCrawlerConfig);
					SingleUrlTask nextStageScriptParameter = CrawlerUtils.newSingleUrlTask(crawler, nextStageUrl, nextStageConfig.optJSONObject(TASK_PARAM) );
					for(Object _key : nextStageParams.names() ) {
						String key = (String) _key;
						JSONArray attrs = nextStageParams.getJSONArray(key);
						if(! Tools.isEmpty(attrs) ) {
							nextStageScriptParameter.addParam(key, attrs.opt(i) );
						}
					}
					
					Runnable nextStage = new CrawlerPipelineTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, stageId+1);
					if(parseAsync) {
						Tools.execute(nextStage);
					} else {
						nextStage.run();
					}
				}
			}
		}
		
		return fetchedResult;
	}
	
	/**
	 * @Name: encapCrawlerConfig 
	 * @Description: ���ݸ��������÷�װcrawlerConfig
	 * 	�����Ҫ��� header, cookie, data, �������crawlerConfig�е�����
	 * 	Ȼ�������config�����õ�header, cookie, data
	 * @param crawlerConfigObj
	 * @param crawlerConfig  
	 * @Create at 2016-09-30 20:25:14 by '970655147'
	 */
	public static void encapCrawlerConfig(JSONObject crawlerConfigObj, CrawlerConfig crawlerConfig) {
		if(! Tools.isEmpty(crawlerConfigObj) ) {
			if(crawlerConfigObj.optBoolean(CLEAR_PREV_HEADERS) ) {
				crawlerConfig.getHeaders().clear();
			}
			if(crawlerConfigObj.optBoolean(CLEAR_PREV_COOKIES) ) {
				crawlerConfig.getCookies().clear();
			}
			if(crawlerConfigObj.optBoolean(CLEAR_PREV_DATA) ) {
				crawlerConfig.getData().clear();
			}
			// headers, cookies, data
			crawlerConfig.addHeaders(crawlerConfigObj.optJSONObject(HEADERS) );
			crawlerConfig.addCookies(crawlerConfigObj.optJSONObject(COOKIES) );
			crawlerConfig.addData(crawlerConfigObj.optJSONObject(DATA) );
		}
	}
	
	// --------------- bean --------------------
	// CrawlerPipeline ��һ��Task
	static class CrawlerPipelineTask implements Runnable {
		private SingleUrlTask scriptParameter;
		private HtmlCrawlerConfig crawlerConfig;
		private JSONArray globalConfig;
		private int stageId;
		public CrawlerPipelineTask(SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig, JSONArray globalConfig, int stageId) {
			this.scriptParameter = scriptParameter;
			this.crawlerConfig = crawlerConfig;
			this.globalConfig = globalConfig;
			this.stageId = stageId;
		}
		@Override
		public void run() {
			try {
				doPipelineParse(scriptParameter, crawlerConfig, globalConfig, stageId);
			} catch (Exception e) {
				err("error while parse : '" + scriptParameter.getUrl() + "', exception : '" + Tools.errorMsg(e) + "'");
			}
		}
	}
	
   // ------------ ���� --------------------

	
}
