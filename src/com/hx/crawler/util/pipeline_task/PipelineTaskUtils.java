package com.hx.crawler.util.pipeline_task;

import com.hx.attrHandler.attrHandler.interf.AttrHandler;
import com.hx.attrHandler.util.AttrHandlerUtils;
import com.hx.crawler.crawler.HtmlCrawler;
import com.hx.crawler.crawler.HtmlCrawlerConfig;
import com.hx.crawler.crawler.SingleUrlTask;
import com.hx.crawler.crawler.interf.Crawler;
import com.hx.crawler.crawler.interf.CrawlerConfig;
import com.hx.crawler.crawler.interf.Page;
import com.hx.crawler.parser.interf.ResultJudger;
import com.hx.crawler.util.CrawlerUtils;
import com.hx.log.util.Constants;
import com.hx.log.util.JSONExtractor;
import com.hx.log.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;

import static com.hx.log.util.Log.err;

/**
 * file name : PipelineTaskUtils.java
 * created at : 14:33  2017-03-11
 * created by 970655147
 */
public final class PipelineTaskUtils {

    // disable constructor
    private PipelineTaskUtils() {
        Tools.assert0("can't instantiate !");
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


    public static void doPipelineTaskAsync(String configPath) throws Exception {
        doPipelineTask(configPath, null, null, true);
    }

    public static void doPipelineTaskAsync(String configPath, SingleUrlTask scriptParameter) throws Exception {
        doPipelineTask(configPath, scriptParameter, null, true);
    }

    public static void doPipelineTaskAsync(String configPath, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(configPath, null, crawlerConfig, true);
    }

    public static void doPipelineTaskAsync(String configPath, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(configPath, scriptParameter, crawlerConfig, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig) throws Exception {
        doPipelineTask(globalConfig, null, null, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig, SingleUrlTask scriptParameter) throws Exception {
        doPipelineTask(globalConfig, scriptParameter, null, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(globalConfig, null, crawlerConfig, true);
    }

    public static void doPipelineTaskAsync(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        doPipelineTask(globalConfig, scriptParameter, crawlerConfig, true);
    }

    public static JSONArray doPipelineTask(String configPath) throws Exception {
        return doPipelineTask(configPath, null, null, false);
    }

    public static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter) throws Exception {
        return doPipelineTask(configPath, scriptParameter, null, false);
    }

    public static JSONArray doPipelineTask(String configPath, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(configPath, null, crawlerConfig, false);
    }

    public static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(configPath, scriptParameter, crawlerConfig, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig) throws Exception {
        return doPipelineTask(globalConfig, null, null, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter) throws Exception {
        return doPipelineTask(globalConfig, scriptParameter, null, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(globalConfig, null, crawlerConfig, false);
    }

    public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig) throws Exception {
        return doPipelineTask(globalConfig, scriptParameter, crawlerConfig, false);
    }

    private static JSONArray doPipelineTask(String configPath, SingleUrlTask scriptParameter,
                                            HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
        JSONArray globalConfig = JSONArray.fromObject(Tools.getContent(configPath));
        return doPipelineTask(globalConfig, scriptParameter, crawlerConfig, async);
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
     * 如果是异步执行任务, res为空Array, 否则 res为给定的任务返回的结果
     * @Create at 2016-09-30 20:02:54 by '970655147'
     */
    public static JSONArray doPipelineTask(JSONArray globalConfig, SingleUrlTask scriptParameter,
                                             HtmlCrawlerConfig crawlerConfig, boolean async) throws Exception {
        Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");

        JSONArray res = new JSONArray();
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
                    nextStageScriptParameter = CrawlerUtils.newSingleUrlTask(crawler, stageConfig.optString(URL),
                            stageConfig.optJSONObject(TASK_PARAM));
                } else {
                    nextStageScriptParameter.addParam(stageConfig.optJSONObject(TASK_PARAM));
                }

                if (async) {
                    Runnable task = new PipelineCrawlTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, i);
                    Tools.execute(task);
                } else {
                    res = doPipelineParse(globalConfig, nextStageScriptParameter, nextStageCrawlerConfig, i);
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
     * @param scriptParameter 当前stage所需要的参数
     * @param crawlerConfig   发送请求所需要的config
     * @param globalConfig    一级一级的任务的配置
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
    public static JSONArray doPipelineParse(JSONArray globalConfig, SingleUrlTask scriptParameter,
                                            HtmlCrawlerConfig crawlerConfig, int stageId) throws Exception {
        Tools.assert0(scriptParameter != null, "'scriptParameter' can't be null ");
        Tools.assert0(crawlerConfig != null, "'crawlerConfig' can't be null ");
        Tools.assert0(globalConfig != null, "'globalConfig' can't be null ");

        // prepare
        if (stageId >= globalConfig.size()) {
            // after finalStage
            return new JSONArray();
        }
        final JSONObject config = globalConfig.getJSONObject(stageId);
        if (Tools.isEmpty(config)) {
            err("got empty config while stageId : '" + stageId + "'");
            return new JSONArray();
        }

        Crawler crawler = scriptParameter.getCrawler();
        String url = scriptParameter.getUrl();
        JSONArray fetchedResult = sendRequestAndFetchResult(scriptParameter, crawlerConfig, config);

        if ((fetchedResult == null) || (fetchedResult.isEmpty())) {
            err("fetched nothing with url : '" + url + "'");
            return new JSONArray();
        }

        if (config.optBoolean(SAVE_FETCHED_RESULT)) {
            saveFetchedResult(fetchedResult, config);
        }

        // process next stage
        if (stageId < globalConfig.size() - 1) {
            processNextStage(globalConfig, fetchedResult, config, crawler, crawlerConfig, stageId);
        }

        return fetchedResult;
    }

    /**
     * 根据当前stage的配置, 发送请求, 并抓取数据
     *
     * @param scriptParameter 当前stage所需要的参数
     * @param crawlerConfig   发送请求所需要的config
     * @param config          当前stage的配置信息
     * @return net.sf.json.JSONArray
     * @throws
     * @author 970655147 created at 2017-03-11 14:16
     */
    private static JSONArray sendRequestAndFetchResult(SingleUrlTask scriptParameter, HtmlCrawlerConfig crawlerConfig,
                                                       final JSONObject config) throws Exception {
        Crawler crawler = scriptParameter.getCrawler();
        String url = scriptParameter.getUrl();
        boolean debugEnable = Boolean.valueOf(scriptParameter.getParam(Tools.DEBUG_ENABLE).toString());

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
        // encap xpathes
        JSONArray xpathesFromConfig = config.getJSONArray(XPATHES);
        String[] xpathes = new String[xpathesFromConfig.size()];
        for (int i = 0, len = xpathesFromConfig.size(); i < len; i++) {
            xpathes[i] = xpathesFromConfig.getString(i);
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
        return fetchedResult;
    }

    /**
     * 如果需要保存抓取到的数据的话, 持久化抓取的数据
     *
     * @param fetchedResult 抓取到的结果
     * @param config        当前stage的配置信息
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 14:24
     */
    private static void saveFetchedResult(JSONArray fetchedResult, JSONObject config) throws Exception {
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

    /**
     * 处理下一个stage的任务, 同步 或者异步
     *
     * @param globalConfig  一级一级的任务的配置
     * @param fetchedResult 当前stage抓取到的数据
     * @param config        当前stage的配置信息
     * @param crawler       crawler
     * @param crawlerConfig 发送请求所需要的config
     * @param stageId       当前stage的id
     * @return void
     * @throws
     * @author 970655147 created at 2017-03-11 14:28
     */
    private static void processNextStage(JSONArray globalConfig, JSONArray fetchedResult, JSONObject config,
                                         Crawler crawler, HtmlCrawlerConfig crawlerConfig, int stageId) {
        String nextStageUrlPat = config.optString(NEXT_STAGE_URL_PATTERN);
        JSONObject nextStageUrlParam = config.optJSONObject(NEXT_STAGE_PARAM_PATTERN);
        boolean parseAsync = config.optBoolean(NEXT_STAGE_PARSE_ASYNC);

        if (!Tools.isEmpty(nextStageUrlPat)) {
            JSONArray nextStageUrls = JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlPat);
            JSONObject nextStageParams = new JSONObject();
            for (Object _key : nextStageUrlParam.names()) {
                String key = (String) _key;
                if (!Tools.isEmpty(key)) {
                    JSONArray val = JSONExtractor.extractInfoFromJSON(fetchedResult, nextStageUrlParam.getString(key));
                    nextStageParams.element(key, val);
                }
            }

            for (int i = 0; i < nextStageUrls.size(); i++) {
                String nextStageUrl = nextStageUrls.getString(i);
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

                Runnable nextStage = new PipelineCrawlTask(nextStageScriptParameter, nextStageCrawlerConfig, globalConfig, stageId + 1);
                if (parseAsync) {
                    Tools.execute(nextStage);
                } else {
                    nextStage.run();
                }
            }
        }
    }

    /**
     * @param crawlerConfigObj 配置中的config对象, 将其中的数据封装到CrawlerConfig中
     * @param crawlerConfig    实际需要封装的CrawlerConfig对象
     * @Name: encapCrawlerConfig
     * @Description: 根据给定的配置封装crawlerConfig
     * 如果需要清除 header, cookie, data, 则先清除crawlerConfig中的数据
     * 然后再添加config中配置的header, cookie, data
     * @Create at 2016-09-30 20:25:14 by '970655147'
     */
    private static void encapCrawlerConfig(JSONObject crawlerConfigObj, CrawlerConfig crawlerConfig) {
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

}
