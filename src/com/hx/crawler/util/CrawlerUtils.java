/**
 * file name : Tools.java
 * created at : 6:58:34 PM Jul 25, 2015
 * created by 970655147
 */

package com.hx.crawler.util;

import static com.hx.log.util.Log.err;
import static com.hx.log.util.Log.info;

import com.hx.crawler.crawler.interf.*;
import com.hx.log.util.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

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
import com.hx.crawler.xpathParser.XPathParser;
import com.hx.crawler.xpathParser.interf.ResultJudger;
import com.hx.log.util.LogPattern.LogPatternChain;

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
    public static final Class[] PARSE_METHOD_PARAMTYPES = new Class[]{ScriptParameter.class};

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
        Tools.save(StringEscapeUtils.unescapeHtml(CrawlerUtils.normalize(html)), file);
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

        for (int i = 0; i < xpathes.length; i++) {
            JSONArray res = getResultByXPath(html, url, xpathes[i]);
            if (!judger.isResultNull(i, res)) {
                return res;
            }
        }

        return null;
    }

    // ���е�unicode surrogates�������ַ�����ʾ
    private static Set<String> surrogates = Tools.asSortedSet();
    // ��һ��ʹ�������ߵ��Ĳ���
    private static String needBeFilterReg = "<!DOCTYPE html>" + "|" + "xmlns=\"http://www.w3.org/1999/xhtml\"";

    static {
        for (int i = 55296; i <= 57343; i++) {
            surrogates.add(String.valueOf(i));
        }
    }

    // ��ʽ��html [ȥ��, ��� ���淶�ı�ǩ]
    // ˼· : ʹ��tagSoup�淶��xml
    // ע�� : ��Ҫ�ض���ContentHandler�����
    // ��Ҫȥ��xml����, ��Ҫȥ��html��ǩ��xmlns������
    public static String normalize(String html) throws Exception {
        Tools.assert0(html != null, "'html' can't be null ");
//		StringReader xmlReader = new StringReader("");
        StringReader sr = new StringReader(html);
        Parser parser = new Parser();        //ʵ����Parse

//		// �����ʽ��֮���xml
        XMLWriter writer = new XMLWriter();    //ʵ����XMLWriter����SAX���ݴ�����
        parser.setContentHandler(writer);    //�������ݴ�����
        StringWriter strWriter = new StringWriter();
        writer.setOutput(strWriter);

        parser.parse(new InputSource(sr));    //����

//		Scanner scan = new PYXScanner();
//		scan.scan(xmlReader, parser);	//ͨ��xmlReader��ȡ������Ľ��

//		char[] buff = new char[1024];
//		StringBuilder sb = new StringBuilder();
//		while(xmlReader.read(buff) != -1) {
//		    sb.append(buff);		//��ӡ������Ľṹ���õ�HTML�ĵ�
//		} 

        // ��ȡ�ṹ���õ�html, ��ȥ��xml����, xml�����Ƿ��ڵ�һ�е�, ��� �����xml�Ļ�, ȥ��xml����
        String res = strWriter.toString();
        int firstLineEnd = res.indexOf("\n");
        if (res.substring(0, firstLineEnd).contains("<?xml")) {
            res = res.substring(firstLineEnd + 1);
        }

        // ������Ϊ�������xmlns������, �������ҵ�xPath��ȡ���������⡣����, ����˵ ֻ��ͨ��/*[@XX='XXX'] ����ȡ����
        // ���Ҷ�ȡ�������� ����һ��xmlns������...						--2015.07.31
//		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><strong><span id=\"ctl01_ContentPlaceHolder1_lblPriceTitle\">ProductPricing</span>:</strong></body></html>";
        // ���� �����res����ͳһ�Ĵ���, ȥ��html��ǩ��xmlns������
        // ���Ӷ���unicode high-surrogates, low-surrogates ��Χ�ڵ�code�Ĵ���..        -- 2017.03.10
        res = res.replaceAll(needBeFilterReg, Tools.EMPTY_STR);
        return trimUnicodeSurrogates(res);
    }

    /**
     * ȥ�����е�unicode surrogate����㷶Χ�ڵ��ַ�, ���� sax�����ᱨ����
     *
     * @param res trim surrogate�����֮ǰ�ĵĽ��
     * @return java.lang.String
     * @throws
     * @author 970655147 created at 2017-03-10 22:21
     */
    private static String trimUnicodeSurrogates(String res) {
        // ��Ҫֱ�������е� surrogates����� &#$code��Ϊ�ָ���, ��Ȼ �ٺ�, ���Ű�.
        Set<String> surrogateSeps = Tools.asSet("&#", ";");
        WordsSeprator sep = new WordsSeprator(res, surrogateSeps, null, true, true);
        StringBuilder sb = new StringBuilder();
        while (sep.hasNext()) {
            String next = sep.next();

            if ("&#".equals(next)) {
                if (sep.hasNext()) {
                    String maySurrogate = sep.next();
                    if (surrogates.contains(maySurrogate)) {
                        // trim ';'
                        sep.next();
                    } else {
                        sb.append(next);
                        sb.append(maySurrogate);
                    }
                    continue;
                }
            }
            sb.append(next);
        }

        return sb.toString();
    }

    // ����һ��ScriptParameter
    public static SingleUrlTask newSingleUrlTask(Crawler<HttpResponse, Header, String, NameValuePair, String, String> crawler, String url, Map<String, Object> param) {
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
        for (String xpath : xpathes) {
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
            if (isStaticMethod) {
                method.invoke(null, singleUrlTask);
            } else {
                method.invoke(clazz.newInstance(), singleUrlTask);
            }
        } catch (Exception e) {
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
        Tools.assert0(!Tools.isEmpty(category), "'category' can't be null ");
        nextStageParams.put(Tools.NAME, category.getString(Tools.NAME));
        nextStageParams.put(Tools.URL, category.getString(Tools.URL));
        nextStageParams.put(Tools.SITE, nextStageParams.getString(Tools.SITE) + "." + category.getString(Tools.NAME));
    }

    // ��ȡtaskName
    public static String getTaskName(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        return "crawl " + singleUrlTask.getParam().get(Tools.TASK) + " from " + singleUrlTask.getParam().get(Tools.SITE);
    }

    // ------------ ��־��� --------------------
    // ��ص�logPattern
    public static LogPatternChain taskBeforeLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_BEFORE_LOG_PATTERN));
    public static LogPatternChain taskAfterLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_AFTER_LOG_PATTERN));
    public static LogPatternChain taskExceptionLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_EXCEPTION_LOG_PATTERN));

    // ��ӡ�������־��Ϣ
    public static void logBeforeTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, boolean debugEnable) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        if (debugEnable) {
            String info = LogPatternUtils.formatLogInfo(taskBeforeLogPatternChain, new JSONObject()
                    .element(Constants.LOG_PATTERN_URL, singleUrlTask.getUrl()).element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
                    .element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.OUT_IDX])
            );
            Log.log(info);
        }
    }

    public static void logBeforeTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String debugEnable) {
        logBeforeTask(singleUrlTask, Boolean.parseBoolean(debugEnable));
    }

    public static void logAfterTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String fetchedResult, String spent, boolean debugEnable) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        if (debugEnable) {
            String info = LogPatternUtils.formatLogInfo(taskAfterLogPatternChain, new JSONObject()
                    .element(Constants.LOG_PATTERN_RESULT, fetchedResult).element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
                    .element(Constants.LOG_PATTERN_SPENT, spent).element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.OUT_IDX])
            );
            Log.log(info);
        }
    }

    public static void logAfterTask(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, String fetchedResult, String spent, String debugEnable) {
        logAfterTask(singleUrlTask, fetchedResult, spent, Boolean.parseBoolean(debugEnable));
    }

    public static void logErrorMsg(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask, Exception e) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        String info = LogPatternUtils.formatLogInfo(taskExceptionLogPatternChain, new JSONObject()
                .element(Constants.LOG_PATTERN_EXCEPTION, e.getClass().getName() + " : " + e.getMessage())
                .element(Constants.LOG_PATTERN_TASK_NAME, CrawlerUtils.getTaskName(singleUrlTask))
                .element(Constants.LOG_PATTERN_URL, singleUrlTask.getUrl()).element(Constants.LOG_PATTERN_MODE, Constants.LOG_MODES[Constants.ERR_IDX])
        );
        Log.err(info);
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
        JSONArray globalConfig = JSONArray.fromObject(Tools.getContent(configPath));
        return doPipelineTask0(globalConfig, scriptParameter, crawlerConfig, async);
    }

    /**
     * @param globalConfig    һ��һ������������
     * @param scriptParameter scriptParameter[ͨ��startUp��������г�ʼ��]
     * @param crawlerConfig   crawlerConfig[ͨ��startUp��������г�ʼ��]
     * @param async           �Ƿ��첽ִ��
     * @return
     * @throws Exception
     * @Name: doPipelineTask0
     * @Description: ��ˮ��ʽ��ִ�и��������õ�����
     * ����globalConfig, �ҵ�startUpΪtrue������, �Ӹ�����ʼִ��
     * ��ʼ��scriptParameter, crawlerConfig, Ȼ������Ƿ��첽ִ������ ����ʹ�õ�ǰ�߳�ִ������, ����ʹ��Tools�е��̳߳�ִ������
     * ������첽ִ������, resΪnull, ���� resΪ���������񷵻صĽ��
     * @Create at 2016-09-30 20:02:54 by '970655147'
     */
    private static JSONArray doPipelineTask0(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
        Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");

        JSONArray res = null;
        for (int i = 0, len = globalConfig.size(); i < len; i++) {
            JSONObject stageConfig = globalConfig.getJSONObject(i);
            if (stageConfig.optBoolean(START_UP)) {
                HtmlCrawlerConfig nextStageCrawlerConfig = crawlerConfig;
                if (crawlerConfig == null) {
                    nextStageCrawlerConfig = HtmlCrawlerConfig.get();
                }
                encapCrawlerConfig(stageConfig.optJSONObject(CRAWLER_CONFIG), nextStageCrawlerConfig);
                SingleUrlTask nextStageScriptParameter = scriptParameter;
                if (scriptParameter == null) {
                    Crawler crawler = HtmlCrawler.getInstance();
                    nextStageScriptParameter = CrawlerUtils.newSingleUrlTask(crawler, stageConfig.optString(URL), stageConfig.optJSONObject(TASK_PARAM));
                } else {
                    nextStageScriptParameter.addParam(stageConfig.optJSONObject(TASK_PARAM));
                }

                if (async) {
                    Runnable task = new CrawlerPipelineTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, i);
                    Tools.execute(task);
                } else {
                    res = doPipelineParse(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, i);
                }
                break;
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
     * @param scriptParameter
     * @param crawlerConfig
     * @param globalConfig
     * @param stageId         ��ǰ������������һ��stage
     * @return ���ص�ǰ����ץȡ���Ľ��
     * @throws Exception
     * @Name: doPipelineParse
     * @Description: ��ˮ��ʽ��ִ�и���������
     * У��scriptParameter, crawerConfig, globalConfig, ��β��֦
     * ��ȡ��ǰ���������, ��У��
     * �Ƿ���Ҫ��ӡ��־, ��ӡץȡ����ʼ����־
     * ��ȡ��ǰ�����xpathes, Ȼ��������õ������������get, post����, У���ȡ���Ľ��, �����Ҫ����ץȡ���ĵ�, �򱣴��ĵ�
     * Ȼ�� ����xpathes�����ĵ�, ���������õ�JUDGERȷ��"�ؼ�������"����[����JUDGER����ResultJudger]
     * У����, �����Ҫ������, �򱣴���
     * <p>
     * ������н�������Ҫִ�е�����, ���fetchedResult�л�ȡ��һ�������url(��), Ȼ��ץȡ��Ҫ�Ĳ���, ��װscriptParameter, �����Ƿ��첽ִ������, ������ʹ�õ�ǰ�߳�ִ������, ����ʹ��Tools�̳߳��е��߳�ִ������
     * @Create at 2016-09-30 20:08:18 by '970655147'
     */
    public static JSONArray doPipelineParse(SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig, JSONArray globalConfig, int stageId) throws Exception {
        Tools.assert0(scriptParameter != null, "'scriptParameter' can't be null ");
        Tools.assert0(crawlerConfig != null, "'crawlerConfig' can't be null ");
        Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");

        // prepare
        if (stageId >= globalConfig.size()) {
            // after finalStage
            return null;
        }
        final JSONObject config = globalConfig.getJSONObject(stageId);
        if (Tools.isEmpty(config)) {
            err("got empty config while stageId : '" + stageId + "'");
            return null;
        }
        Crawler crawler = scriptParameter.getCrawler();
        String url = scriptParameter.getUrl();
        boolean debugEnable = Boolean.valueOf(scriptParameter.getParam(Tools.DEBUG_ENABLE).toString());

        // encap xpathes
        JSONArray xpathesFromConfig = config.getJSONArray(XPATHES);
        String[] xpathes = new String[xpathesFromConfig.size()];
        for (int i = 0, len = xpathesFromConfig.size(); i < len; i++) {
            xpathes[i] = xpathesFromConfig.getString(i);
        }

        long start = System.currentTimeMillis();
        CrawlerUtils.logBeforeTask(scriptParameter, debugEnable);

        // encap config, post requests
        encapCrawlerConfig(config.optJSONObject(CRAWLER_CONFIG), crawlerConfig);
        Page<HttpResponse> page = null;
        if (Tools.GET.equalsIgnoreCase(config.optString(METHOD))) {
            page = crawler.getPage(url, crawlerConfig);
        } else if (Tools.POST.equalsIgnoreCase(config.optString(METHOD))) {
            page = crawler.postPage(url, crawlerConfig);
        }
        if (page == null) {
            err("got empty page while fetch : '" + url + "'");
            return null;
        }

        // get result by xpathes
        String html = page.getContent();
        if (config.optBoolean(SAVE_PREPARE_DOC)) {
            CrawlerUtils.getPreparedDoc(url, html, SAVE_PREPARED_DOC_PATH);
        }
        JSONArray fetchedResult = CrawlerUtils.getResultByXPathes(html, url, xpathes, new ResultJudger() {
            @Override
            public boolean isResultNull(int idx, JSONArray fetchedData) {
                JSONArray judgersConfig = config.optJSONArray(JUDGER);
                int gotResNum = -1;
                for (Object _judger : judgersConfig) {
                    String judgerPat = _judger.toString();
                    if (!Tools.isEmpty(judgerPat)) {
                        JSONArray arr = JSONExtractor.extractInfoFromJSON(fetchedData, judgerPat);
                        if (Tools.isEmpty(arr)) {
                            return true;
                        }

                        if (gotResNum < 0) {
                            gotResNum = arr.size();
                        } else {
                            if (gotResNum != arr.size()) {
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

        if (fetchedResult == null) {
            err("fetched nothing with url : '" + url + "'");
            return null;
        }

        if (config.optBoolean(SAVE_FETCHED_RESULT)) {
//			Tools.append(String.valueOf(fetchedResult), SAVE_FETCHED_RESULT_PATH);
            JSONArray extractedFetchedResult = fetchedResult;
            if (!Tools.isEmpty(config.optString(EXTRACT_FETCHED_RESULT))) {
                extractedFetchedResult = JSONExtractor.extractInfoFromJSON(extractedFetchedResult, config.optString(EXTRACT_FETCHED_RESULT));
            }

            String saveFetchedResult = String.valueOf(extractedFetchedResult);
            if (!Tools.isEmpty(config.optString(FETCHED_RESULT_MAPPER))) {
                AttrHandler handler = AttrHandlerUtils.handlerParse(config.optString(FETCHED_RESULT_MAPPER), Constants.HANDLER);
                saveFetchedResult = handler.handle(String.valueOf(saveFetchedResult));
            }
            Tools.appendBufferCRLF(DO_PIPELINE_TASK_NAME, String.valueOf(saveFetchedResult));
        }

        // process next stage
        if (stageId < globalConfig.size() - 1) {
            String nextStageUrlPat = config.optString(NEXT_STAGE_URL_PATTERN);
            JSONObject nextStageUrlParam = config.optJSONObject(NEXT_STAGE_PARAM_PATTERN);
            boolean parseAsync = config.optBoolean(NEXT_STAGE_PARSE_ASYNC);

            if (!Tools.isEmpty(nextStageUrlPat)) {
                JSONArray nextStageUrls = JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlPat);
                JSONObject nextStageParams = new JSONObject();
                for (Object _key : nextStageUrlParam.names()) {
                    String key = (String) _key;
                    if (!Tools.isEmpty(key)) {
                        nextStageParams.element(key, JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlParam.getString(key)));
                    }
                }

                for (int i = 0; i < nextStageUrls.size(); i++) {
                    String nextStageUrl = (String) nextStageUrls.getString(i);
                    JSONObject nextStageConfig = globalConfig.getJSONObject(stageId + 1);
                    HtmlCrawlerConfig nextStageCrawlerConfig = new HtmlCrawlerConfig(crawlerConfig);
                    // duplicate, rm
                    // encapCrawlerConfig(nextStageConfig.optJSONObject(CRAWLER_CONFIG), nextStageCrawlerConfig);
                    SingleUrlTask nextStageScriptParameter = CrawlerUtils.newSingleUrlTask(crawler, nextStageUrl, nextStageConfig.optJSONObject(TASK_PARAM));
                    for (Object _key : nextStageParams.names()) {
                        String key = (String) _key;
                        JSONArray attrs = nextStageParams.getJSONArray(key);
                        if (!Tools.isEmpty(attrs)) {
                            nextStageScriptParameter.addParam(key, attrs.opt(i));
                        }
                    }

                    Runnable nextStage = new CrawlerPipelineTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, stageId + 1);
                    if (parseAsync) {
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
     * @param crawlerConfigObj
     * @param crawlerConfig
     * @Name: encapCrawlerConfig
     * @Description: ���ݸ��������÷�װcrawlerConfig
     * �����Ҫ��� header, cookie, data, �������crawlerConfig�е�����
     * Ȼ�������config�����õ�header, cookie, data
     * @Create at 2016-09-30 20:25:14 by '970655147'
     */
    public static void encapCrawlerConfig(JSONObject crawlerConfigObj, CrawlerConfig crawlerConfig) {
        if (!Tools.isEmpty(crawlerConfigObj)) {
            if (crawlerConfigObj.optBoolean(CLEAR_PREV_HEADERS)) {
                crawlerConfig.getHeaders().clear();
            }
            if (crawlerConfigObj.optBoolean(CLEAR_PREV_COOKIES)) {
                crawlerConfig.getCookies().clear();
            }
            if (crawlerConfigObj.optBoolean(CLEAR_PREV_DATA)) {
                crawlerConfig.getData().clear();
            }
            // headers, cookies, data
            crawlerConfig.addHeaders(crawlerConfigObj.optJSONObject(HEADERS));
            crawlerConfig.addCookies(crawlerConfigObj.optJSONObject(COOKIES));
            crawlerConfig.addData(crawlerConfigObj.optJSONObject(DATA));
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


    // ------------ recurselyTask���  add at 2017.03.10 --------------------

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback) {
        recurselyTask(seedUrl, method, config, callback, true);
    }

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, boolean isBfs) {
        recurselyTask(seedUrl, method, config, callback, false, isBfs);
    }

    /**
     * ����һ��seedUrl, �Լ���ص���Ҫcontext, �Լ��õ�page֮��Ļص�
     * callback��ץȡ����, �Լ��õ���Ҫ��������, ��ӵ�todo��, Ȼ�� ��������
     * ���ϵĴ���ȥ
     *
     * @param seedUrl  ����url
     * @param method   url����ķ�ʽ
     * @param config   url������Ҫ��context
     * @param callback �õ�page֮����Ļص�
     * @param isBfs    bfs or dfs
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-10 19:03
     */
    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, boolean isAsync, boolean isBfs) {
        Tools.assert0(seedUrl != null, "'seedUrl' can't be null ");
        Tools.assert0(method != null, "'method' can't be null ");
        Tools.assert0(config != null, "'config' can't be null ");
        Tools.assert0(callback != null, "'callback' can't be null ");

        final HttpMethod fMethod = method;
        final CrawlerConfig fConfig = config;
        final RecurseCrawlCallback fCallback = callback;
        final RecurseTaskList<RecurseCrawlTask> todo = new RecurseTraskListImpl(isBfs);
        RecurseCrawlTask seedTask = new RecurseCrawlTaskImpl(seedUrl, 0, method, config);
        todo.add(seedTask);

        while (!todo.isEmpty()) {
            final RecurseCrawlTask task = todo.take();
            if (!isAsync) {
                recurselyTask0(task, method, config, callback, todo);
            } else {
                Tools.execute(new Runnable() {
                    @Override
                    public void run() {
                        recurselyTask0(task, fMethod, fConfig, fCallback, todo);
                    }
                });
            }
        }
    }


    /**
     * ����recurselyTask0�ĺ���ҵ��, ��������, ������callback
     *
     * @param task     ��ǰ����
     * @param method   ��ǰ��������ķ�ʽ
     * @param config   ��ǰ������Ҫ�Ĳ���
     * @param callback �������Ļص�
     * @param todo     �����б�
     * @return boolean
     * @throws
     * @author 970655147 created at 2017-03-10 22:43
     */
    private static boolean recurselyTask0(RecurseCrawlTask task, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, RecurseTaskList<RecurseCrawlTask> todo) {
        if (task == null) {
            Log.err("got an 'null' task, ignore ");
            return false;
        }

        Page page = null;
        task.setRunTime(System.currentTimeMillis());
        try {
            page = dispatchSendPost(task, task.getMethod());
        } catch (Exception e) {
            Log.err("error while send post for url : " + task.getUrl() + ", with config : " + task.getConfig());
            page = null;
        }
        task.setFinishTime(System.currentTimeMillis());
        task.setPage(page);

        if (page == null) {
            // logged
            return false;
        }

        callback.run(new RecurseCrawlTaskFacadeImpl(task), todo);
        return true;
    }

    /**
     * ���ݸ�������parent, url, �Լ���ص����ô���RecurseCrawlTaskImpl
     * �˴��������û��Ĵ���ʵ����Ψһ���
     *
     * @param parent ��ǰ���ĸ��ڵ�
     * @param url    ��ǰ����url
     * @param method ��ǰ��������ʽ
     * @param config ��ǰurl����Ҫ�Ĳ���
     * @return com.hx.crawler.util.CrawlerUtils.RecurseCrawlTaskImpl
     * @throws
     * @author 970655147 created at 2017-03-10 19:43
     */
    public static RecurseCrawlTask newRecurseCrawlTask(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config) {
        int depth = (parent == null) ? 0 : parent.getDepth() + 1;
        return new RecurseCrawlTaskImpl(parent, url, depth, method, config);
    }

    /**
     * ���ݸ�����task, ��������.
     *
     * @param task   ������task
     * @param method task��Ӧ��������Ҫ����ķ�ʽ
     * @return com.hx.crawler.crawler.interf.Page
     * @throws
     * @author 970655147 created at 2017-03-10 18:58
     */
    private static Page dispatchSendPost(RecurseCrawlTask task, HttpMethod method) throws Exception {
        Page page = null;
        if (method == null) {
            Log.err("got an 'null' method while send post for url : " + task.getUrl() + ", with config : " + task.getConfig());
        } else {
            Crawler crawler = HtmlCrawler.getInstance();
            if (HttpMethod.GET == method) {
                page = crawler.getPage(task.getUrl(), task.getConfig());
            } else if (HttpMethod.POST == method) {
                page = crawler.postPage(task.getUrl(), task.getConfig());
            } else if (HttpMethod.PUT == method) {
                page = crawler.putPage(task.getUrl(), task.getConfig());
            } else if (HttpMethod.DELETE == method) {
                page = crawler.deletePage(task.getUrl(), task.getConfig());
            } else if ((HttpMethod.HEAD == method) || (HttpMethod.TRACE == method)) {
                page = crawler.getPage(task.getUrl(), task.getConfig());
            } else {
                Log.err("got an unknown method while send post for url : " + task.getUrl() + ", with config : " + task.getConfig());
            }
        }

        return page;
    }

    /**
     * �ݹ���ȡ���ݵ�task
     *
     * @author 970655147
     */
    public interface RecurseCrawlTask {
        String getUrl();

        int getDepth();

        HttpMethod getMethod();

        CrawlerConfig getConfig();

        Page getPage();

        long getCreateTime();

        long getRunTime();

        long getFinishTime();

        void setPage(Page page);

        void setCreateTime(long createTime);

        void setRunTime(long runTime);

        void setFinishTime(long finishTime);
    }

    /**
     * �ݹ���ȡ���ݵ�task��facde, ���û�
     *
     * @author 970655147
     */
    public interface RecurseCrawlTaskFacade {
        String getUrl();

        int getDepth();

        HttpMethod getMethod();

        CrawlerConfig getConfig();

        Page getPage();

        long getCreateTime();

        long getRunTime();

        long getFinishTime();
    }

    public interface RecurseCrawlCallback<Task> {

        /**
         * ����ǰҳ��, �ɼ�����, �������ȵ�
         *
         * @param task ��ǰ������Ϣ
         * @param todo �����б�
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 18:14
         */
        void run(RecurseCrawlTaskFacade task, RecurseTaskList<Task> todo);

    }


    /**
     * �������
     *
     * @author 970655147 created at 2017-03-10 17:14
     */
    public interface RecurseTaskList<Task> {

        /**
         * ��������������һ��task
         *
         * @param task ����������
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:23
         */
        void add(Task task);

        /**
         * �����������ȡ��һ��
         *
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:23
         */
        Task take();

        /**
         * ��ǰ���������������
         *
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:58
         */
        int size();

        /**
         * ��ǰ�����Ƿ�Ϊ��
         *
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:59
         */
        boolean isEmpty();

    }

    /**
     * �ݹ���ȡ���ݵ�task�ļ�ʵ��
     *
     * @author 970655147
     */
    static class RecurseCrawlTaskImpl implements RecurseCrawlTask {
        RecurseCrawlTaskFacade parent;
        String url;
        int depth;
        HttpMethod method;
        CrawlerConfig config;
        Page page;
        long createTime;
        long runTime;
        long finishTime;

        /**
         * ��ʼ��
         */
        RecurseCrawlTaskImpl(String url, int depth, HttpMethod method, CrawlerConfig config) {
            this(null, url, depth, method, config);
        }

        RecurseCrawlTaskImpl(RecurseCrawlTaskFacade parent, String url, int depth, HttpMethod method, CrawlerConfig config) {
            super();
            Tools.assert0(url != null, "url can't be null ! ");
            Tools.assert0(depth >= 0, "depth must gt 0 ! ");
            Tools.assert0(method != null, "method can't be null ! ");
            Tools.assert0(config != null, "config can't be null ! ");

            this.parent = parent;
            this.url = url;
            this.depth = depth;
            this.method = method;
            this.config = config;
        }

        /**
         * getter
         */
        public String getUrl() {
            return url;
        }

        public int getDepth() {
            return depth;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public CrawlerConfig getConfig() {
            return config;
        }

        public Page getPage() {
            return page;
        }

        public long getCreateTime() {
            return createTime;
        }

        public long getRunTime() {
            return runTime;
        }

        public long getFinishTime() {
            return finishTime;
        }

        /**
         * setter
         */
        public void setPage(Page page) {
            this.page = page;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setRunTime(long runTime) {
            this.runTime = runTime;
        }

        public void setFinishTime(long finishTime) {
            this.finishTime = finishTime;
        }
    }

    /**
     * �ݹ���ȡ���ݵ�taskFacade�ļ�ʵ��
     *
     * @author 970655147
     */
    static class RecurseCrawlTaskFacadeImpl implements RecurseCrawlTaskFacade {
        private RecurseCrawlTask task;

        /**
         * ��ʼ��
         */
        public RecurseCrawlTaskFacadeImpl(RecurseCrawlTask task) {
            Tools.assert0(task != null, "task can't be null !");
            this.task = task;
        }

        @Override
        public String getUrl() {
            return task.getUrl();
        }

        @Override
        public int getDepth() {
            return task.getDepth();
        }

        @Override
        public HttpMethod getMethod() {
            return task.getMethod();
        }

        @Override
        public CrawlerConfig getConfig() {
            return task.getConfig();
        }

        @Override
        public Page getPage() {
            return task.getPage();
        }

        @Override
        public long getCreateTime() {
            return task.getCreateTime();
        }

        @Override
        public long getRunTime() {
            return task.getRunTime();
        }

        @Override
        public long getFinishTime() {
            return task.getFinishTime();
        }
    }

    /**
     * file name : CrawlerUtils.java
     * Created by 970655147 on 2017-03-10 17:25.
     */
    static class RecurseTraskListImpl implements RecurseTaskList<RecurseCrawlTask> {
        private Deque<RecurseCrawlTask> list;
        private boolean bfs;

        /**
         * ��ʼ��
         */
        public RecurseTraskListImpl(boolean bfs) {
            this(bfs, 10);
        }

        public RecurseTraskListImpl(boolean bfs, int estimateSize) {
            this.bfs = bfs;
            if (bfs) {
                list = new LinkedList<>();
            } else {
                list = new ArrayDeque<>(estimateSize);
            }
        }

        @Override
        public void add(RecurseCrawlTask recurseCrawlTask) {
            synchronized (this) {
                if (bfs) {
                    list.addFirst(recurseCrawlTask);
                } else {
                    list.addLast(recurseCrawlTask);
                }
            }
        }

        @Override
        public RecurseCrawlTask take() {
            if (list.isEmpty()) {
                return null;
            }

            synchronized (this) {
                if (list.isEmpty()) {
                    return null;
                }
                return list.removeLast();
            }
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }
    }

    // ------------ ���� --------------------


}
