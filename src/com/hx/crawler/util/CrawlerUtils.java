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

// 工具类
public final class CrawlerUtils {

    // disable constructor
    private CrawlerUtils() {
        Tools.assert0("can't instantiate !");
    }

    // parse 相关常量
    public static final String PARSE_METHOD_NAME = "parse";
    public static final boolean IS_PARSE_METHOD_STATIC = true;
    public static final Class[] PARSE_METHOD_PARAMTYPES = new Class[]{ScriptParameter.class};

    // ----------------- crawler 处理相关 -----------------------
    // 获取处理过之后的文档
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

    // 通过xpath 获取结果
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

    // 所有的unicode surrogates代码点的字符串表示
    private static Set<String> surrogates = Tools.asSortedSet();
    // 第一次使用正则踢掉的部分
    private static String needBeFilterReg = "<!DOCTYPE html>" + "|" + "xmlns=\"http://www.w3.org/1999/xhtml\"";

    static {
        for (int i = 55296; i <= 57343; i++) {
            surrogates.add(String.valueOf(i));
        }
    }

    // 格式化html [去掉, 添加 不规范的标签]
    // 思路 : 使用tagSoup规范化xml
    // 注意 : 需要重定向ContentHandler的输出
    // 需要去掉xml声明, 需要去掉html标签的xmlns的属性
    public static String normalize(String html) throws Exception {
        Tools.assert0(html != null, "'html' can't be null ");
//		StringReader xmlReader = new StringReader("");
        StringReader sr = new StringReader(html);
        Parser parser = new Parser();        //实例化Parse

//		// 输出格式化之后的xml
        XMLWriter writer = new XMLWriter();    //实例化XMLWriter，即SAX内容处理器
        parser.setContentHandler(writer);    //设置内容处理器
        StringWriter strWriter = new StringWriter();
        writer.setOutput(strWriter);

        parser.parse(new InputSource(sr));    //解析

//		Scanner scan = new PYXScanner();
//		scan.scan(xmlReader, parser);	//通过xmlReader读取解析后的结果

//		char[] buff = new char[1024];
//		StringBuilder sb = new StringBuilder();
//		while(xmlReader.read(buff) != -1) {
//		    sb.append(buff);		//打印解析后的结构良好的HTML文档
//		} 

        // 获取结构良好的html, 并去掉xml声明, xml声明是放在第一行的, 因此 如果是xml的话, 去掉xml声明
        String res = strWriter.toString();
        int firstLineEnd = res.indexOf("\n");
        if (res.substring(0, firstLineEnd).contains("<?xml")) {
            res = res.substring(firstLineEnd + 1);
        }

        // 就是因为多了这个xmlns的声明, 导致了我的xPath读取数据有问题。。。, 或者说 只能通过/*[@XX='XXX'] 来读取数据
        // 并且读取到的数据 还有一个xmlns的声明...						--2015.07.31
//		String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><strong><span id=\"ctl01_ContentPlaceHolder1_lblPriceTitle\">ProductPricing</span>:</strong></body></html>";
        // 所以 这里对res进行统一的处理, 去掉html标签的xmlns的声明
        // 增加对于unicode high-surrogates, low-surrogates 范围内的code的处理..        -- 2017.03.10
        res = res.replaceAll(needBeFilterReg, Tools.EMPTY_STR);
        return trimUnicodeSurrogates(res);
    }

    /**
     * 去掉所有的unicode surrogate代码点范围内的字符, 否则 sax解析会报错误
     *
     * @param res trim surrogate代码点之前的的结果
     * @return java.lang.String
     * @throws
     * @author 970655147 created at 2017-03-10 22:21
     */
    private static String trimUnicodeSurrogates(String res) {
        // 不要直接以所有的 surrogates代码点 &#$code作为分隔符, 不然 嘿嘿, 等着吧.
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

    // 创建一个ScriptParameter
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

    // 通过xpath获取真实的xpath
    // 可以改写为JSONArray.toString() 实现 [思路来自'duncen'[newEgg同事] ]
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

    // 利用反射调用指定的class的methodName方法
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

    // 为nextStageParams添加category
    public static void addNameUrlSite(JSONObject category, JSONObject nextStageParams) {
        Tools.assert0(!Tools.isEmpty(category), "'category' can't be null ");
        nextStageParams.put(Tools.NAME, category.getString(Tools.NAME));
        nextStageParams.put(Tools.URL, category.getString(Tools.URL));
        nextStageParams.put(Tools.SITE, nextStageParams.getString(Tools.SITE) + "." + category.getString(Tools.NAME));
    }

    // 获取taskName
    public static String getTaskName(ScriptParameter<?, ?, ?, ?, ?, ?> singleUrlTask) {
        Tools.assert0(singleUrlTask != null, "'singleUrlTask' can't be null ");
        return "crawl " + singleUrlTask.getParam().get(Tools.TASK) + " from " + singleUrlTask.getParam().get(Tools.SITE);
    }

    // ------------ 日志相关 --------------------
    // 相关的logPattern
    public static LogPatternChain taskBeforeLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_BEFORE_LOG_PATTERN));
    public static LogPatternChain taskAfterLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_AFTER_LOG_PATTERN));
    public static LogPatternChain taskExceptionLogPatternChain = LogPatternUtils.initLogPattern(Constants.optString(Constants._TASK_EXCEPTION_LOG_PATTERN));

    // 打印任务的日志信息
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

    // ------------ PipelineTask相关  add at 2016.08.13 --------------------
    // PipelineTask相关常量
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

    // 其他配置
    public static String SAVE_PREPARED_DOC_PATH = Tools.getTmpPath("preparedDoc", Tools.HTML);
    public static String SAVE_FETCHED_RESULT_PATH = Tools.getTmpPath("fetchedResult", Tools.TXT);
    public static final String DO_PIPELINE_TASK_NAME = "doPipelineTask";

    // 获取给定的configPath对应的配置, 然后 执行流水线式的执行任务
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
     * @param globalConfig    一级一级的任务配置
     * @param scriptParameter scriptParameter[通过startUp的任务进行初始化]
     * @param crawlerConfig   crawlerConfig[通过startUp的任务进行初始化]
     * @param async           是否异步执行
     * @return
     * @throws Exception
     * @Name: doPipelineTask0
     * @Description: 流水线式的执行给定的配置的任务
     * 遍历globalConfig, 找到startUp为true的任务, 从该任务开始执行
     * 初始化scriptParameter, crawlerConfig, 然后根据是否异步执行任务 决定使用当前线程执行任务, 还是使用Tools中的线程池执行任务
     * 如果是异步执行任务, res为null, 否则 res为给定的任务返回的结果
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
     * @param stageId         当前的任务属于哪一个stage
     * @return 返回当前任务抓取到的结果
     * @throws Exception
     * @Name: doPipelineParse
     * @Description: 流水线式的执行给定的任务
     * 校验scriptParameter, crawerConfig, globalConfig, 收尾剪枝
     * 获取当前任务的配置, 并校验
     * 是否需要打印日志, 打印抓取任务开始的日志
     * 获取当前任务的xpathes, 然后根据配置的请求决定发送get, post请求, 校验获取到的结果, 如果需要保存抓取的文档, 则保存文档
     * 然后 根据xpathes解析文档, 并根据配置的JUDGER确保"关键的数据"存在[根据JUDGER构造ResultJudger]
     * 校验结果, 如果需要保存结果, 则保存结果
     * <p>
     * 如果还有接下来需要执行的任务, 则从fetchedResult中获取下一个任务的url(们), 然后抓取需要的参数, 封装scriptParameter, 根据是否异步执行任务, 决定是使用当前线程执行任务, 还是使用Tools线程池中的线程执行任务
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
     * @Description: 根据给定的配置封装crawlerConfig
     * 如果需要清除 header, cookie, data, 则先清除crawlerConfig中的数据
     * 然后再添加config中配置的header, cookie, data
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
    // CrawlerPipeline 的一个Task
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


    // ------------ recurselyTask相关  add at 2017.03.10 --------------------

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback) {
        recurselyTask(seedUrl, method, config, callback, true);
    }

    public static void recurselyTask(String seedUrl, HttpMethod method, CrawlerConfig config, RecurseCrawlCallback callback, boolean isBfs) {
        recurselyTask(seedUrl, method, config, callback, false, isBfs);
    }

    /**
     * 传入一个seedUrl, 以及相关的需要context, 以及拿到page之后的回调
     * callback中抓取数据, 以及拿到需要的子任务, 添加到todo中, 然后 迭代迭代
     * 不断的传下去
     *
     * @param seedUrl  种子url
     * @param method   url请求的方式
     * @param config   url请求需要的context
     * @param callback 拿到page之后处理的回调
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
     * 处理recurselyTask0的核心业务, 发送请求, 并处理callback
     *
     * @param task     当前任务
     * @param method   当前任务请求的方式
     * @param config   当前任务需要的参数
     * @param callback 处理结果的回调
     * @param todo     任务列表
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
     * 根据给定个的parent, url, 以及相关的配置创建RecurseCrawlTaskImpl
     * 此处是留给用户的创建实例的唯一入口
     *
     * @param parent 当前结点的父节点
     * @param url    当前结点的url
     * @param method 当前结点的请求方式
     * @param config 当前url所需要的参数
     * @return com.hx.crawler.util.CrawlerUtils.RecurseCrawlTaskImpl
     * @throws
     * @author 970655147 created at 2017-03-10 19:43
     */
    public static RecurseCrawlTask newRecurseCrawlTask(RecurseCrawlTaskFacade parent, String url, HttpMethod method, CrawlerConfig config) {
        int depth = (parent == null) ? 0 : parent.getDepth() + 1;
        return new RecurseCrawlTaskImpl(parent, url, depth, method, config);
    }

    /**
     * 根据给定的task, 发送请求.
     *
     * @param task   给定的task
     * @param method task对应的任务需要请求的方式
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
     * 递归爬取数据的task
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
     * 递归爬取数据的task的facde, 给用户
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
         * 处理当前页面, 采集数据, 添加任务等等
         *
         * @param task 当前任务信息
         * @param todo 任务列表
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 18:14
         */
        void run(RecurseCrawlTaskFacade task, RecurseTaskList<Task> todo);

    }


    /**
     * 任务队列
     *
     * @author 970655147 created at 2017-03-10 17:14
     */
    public interface RecurseTaskList<Task> {

        /**
         * 向任务队列中添加一个task
         *
         * @param task 给定的任务
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:23
         */
        void add(Task task);

        /**
         * 从任务队列中取出一个
         *
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:23
         */
        Task take();

        /**
         * 当前队列中任务的数量
         *
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:58
         */
        int size();

        /**
         * 当前队列是否为空
         *
         * @return
         * @throws
         * @author 970655147 created at 2017-03-10 17:59
         */
        boolean isEmpty();

    }

    /**
     * 递归爬取数据的task的简单实现
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
         * 初始化
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
     * 递归爬取数据的taskFacade的简单实现
     *
     * @author 970655147
     */
    static class RecurseCrawlTaskFacadeImpl implements RecurseCrawlTaskFacade {
        private RecurseCrawlTask task;

        /**
         * 初始化
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
         * 初始化
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

    // ------------ 待续 --------------------


}
